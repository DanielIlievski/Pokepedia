package com.echo.pokepedia.data.repository

import com.echo.pokepedia.R
import com.echo.pokepedia.data.model.User
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.domain.repository.AuthRepository
import com.echo.pokepedia.util.ResourceProvider
import com.google.android.gms.tasks.Task
import com.echo.pokepedia.util.USERS_COLLECTION
import com.google.firebase.firestore.CollectionReference
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val resourceProvider: ResourceProvider,
    @Named(USERS_COLLECTION) private val users: CollectionReference,
    private val coroutineScope: CoroutineScope
) : AuthRepository {

    override suspend fun getCurrentUser(): NetworkResult<FirebaseUser?> {
        return try {
            NetworkResult.Success(firebaseAuth.currentUser)
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(e)
        }
    }

    override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                if (result.user!!.isEmailVerified) {
                    NetworkResult.Success(result.user!!)
                } else {
                    NetworkResult.Failure(Exception(resourceProvider.fetchString(R.string.verify_email)))
                }
            } else {
                NetworkResult.Failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(e)
        }
    }

    override suspend fun googleSignIn(task: Task<GoogleSignInAccount>): NetworkResult<FirebaseUser?> {
        return try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { taskResult ->
                        if (taskResult.isSuccessful) {
                            val user = taskResult.result.user
                            coroutineScope.launch {
                                addUserToFirestore(user)
                            }
                        }
                    }.await()
                NetworkResult.Success(result.user)
            } else {
                NetworkResult.Failure(Exception("Google sign in failed"))
            }

        } catch (e: ApiException) {
            NetworkResult.Failure(e)
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResult<FirebaseUser> {
        return try {
            val user = createNewUser(firstName, lastName, email, password)
            user?.sendEmailVerification()

            if (user != null) {
                addUserToFirestore(user)
                NetworkResult.Success(user)
            } else {
                NetworkResult.Failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(e)
        }
    }

    override suspend fun logout(): NetworkResult<Boolean> {
        return try {
            firebaseAuth.signOut()
            NetworkResult.Success(true)
        } catch (e: Exception) {
            NetworkResult.Failure(e)
        }
    }

    // region auxiliary methods
    private suspend fun createNewUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): FirebaseUser? {
        val username = firstName + lastName
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        result?.user?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(username).build()
        )?.await()
        return result.user
    }

    private suspend fun addUserToFirestore(user: FirebaseUser?) {
        val email = user?.email ?: ""
        val fullName = user?.displayName ?: ""
        val uid = user?.uid ?: ""
        val newUser = User(fullName, email, Timestamp.now(), uid)
        users.document(uid).set(newUser).await()
    }
    // endregion

}