package com.echo.pokepedia.data.repository

import com.echo.pokepedia.R
import com.echo.pokepedia.data.model.User
import com.echo.pokepedia.util.Resource
import com.echo.pokepedia.domain.repository.AuthRepository
import com.echo.pokepedia.util.ResourceProvider
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
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
                            val user = taskResult.result.user

                            coroutineScope.launch {
                                addUserToFirestore(user)
                            }
                        }
                    }.await()
                Resource.Success(result.user)
            } else {
                Resource.Failure(Exception(resourceProvider.fetchString(R.string.verify_email)))
            }

        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun facebookSignIn(token: AccessToken): Resource<FirebaseUser?> {
        return try {
            val credential = FacebookAuthProvider.getCredential(token.token)
            val result = firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { taskResult ->
                    if (taskResult.isSuccessful) {
                        val user = taskResult.result.user

                        coroutineScope.launch {
                            addUserToFirestore(user)
                        }
                    }
                }.await()
            Resource.Success(result.user)
        } catch (e: Exception) {
            e.printStackTrace()
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
            user?.sendEmailVerification()

            if (user != null) {
                addUserToFirestore(user)
                Resource.Success(user)
            } else {
                Resource.Failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Boolean> {
        return try {
            val isEmailPasswordProvider =
                firebaseAuth.currentUser?.providerData?.any { it.providerId == "password" } ?: false

            if (isEmailPasswordProvider) {
                val result = firebaseAuth.sendPasswordResetEmail(email)
                result.await()

                if (result.isSuccessful) {
                    Resource.Success(true)
                } else {
                    Resource.Failure(Exception("Password reset failed"))
                }
            } else {
                Resource.Failure(Exception("Your provider is not email/password"))
            }
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
    ): FirebaseUser? {
        val username = firstName + lastName
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        result?.user?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(username).build()
        )?.await()
        return result.user
    }

    private suspend fun addUserToFirestore(user: FirebaseUser?) {
        val fullName = user?.displayName ?: ""
        val email = user?.email ?: ""
        val uid = user?.uid ?: ""
        val newUser = User(fullName, email, Timestamp.now(), uid)

        firebaseFirestore.collection("users").document(uid).set(newUser).await()
    }
    // endregion

}