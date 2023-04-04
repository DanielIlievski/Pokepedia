package com.echo.pokepedia.data.repository

import com.echo.pokepedia.R
import com.echo.pokepedia.data.model.User
import com.echo.pokepedia.util.Resource
import com.echo.pokepedia.domain.repository.AuthRepository
import com.echo.pokepedia.util.ResourceProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val resourceProvider: ResourceProvider,
    private val firebaseFirestore: FirebaseFirestore,
    private val coroutineScope: CoroutineScope
) : AuthRepository {

    override suspend fun getCurrentUser(): Resource<FirebaseUser?> {
        return try {
            Resource.Success(firebaseAuth.currentUser)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (firebaseAuth.currentUser!!.isEmailVerified) {
                Resource.Success(result.user!!)
            } else {
                Resource.Failure(Exception(resourceProvider.fetchString(R.string.verify_email)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun googleSignIn(task: Task<GoogleSignInAccount>): Resource<FirebaseUser?> {
        return try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { taskResult ->
                        if (taskResult.isSuccessful) {
                            val email = account.email ?: ""
                            val fullName = account.displayName ?: ""
                            val uid = account.id ?: ""
                            coroutineScope.launch {
                                addUserToFirestore(fullName, email, uid)
                            }
                        }
                    }.await()
                Resource.Success(result.user)
            } else {
                Resource.Failure(Exception(resourceProvider.fetchString(R.string.verify_email)))
            }

        } catch (e: ApiException) {
            Resource.Failure(e)
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val user = createNewUser(firstName, lastName, email, password)

            val fullName = "$firstName $lastName"
            addUserToFirestore(fullName, email, user.uid)

            firebaseAuth.currentUser?.sendEmailVerification()

            Resource.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    // region auxiliary methods
    private suspend fun createNewUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): FirebaseUser {
        val username = firstName + lastName
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        result?.user?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(username).build()
        )?.await()
        return result.user!!
    }
    private suspend fun addUserToFirestore(
        fullName: String,
        email: String,
        uid: String,
    ) {
        val newUser = User(fullName, email, Timestamp.now(), uid)
        firebaseFirestore.collection("users").document(uid).set(newUser).await()
    }
    // endregion

}