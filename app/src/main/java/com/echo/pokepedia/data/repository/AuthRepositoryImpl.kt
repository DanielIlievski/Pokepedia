package com.echo.pokepedia.data.repository

import com.echo.pokepedia.R
import com.echo.pokepedia.data.model.User
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.facebook.AccessToken
import com.echo.pokepedia.util.USERS_COLLECTION
import com.echo.pokepedia.util.UiText
import com.google.firebase.firestore.CollectionReference
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @Named(USERS_COLLECTION) private val users: CollectionReference,
    private val coroutineScope: CoroutineScope
) : AuthRepository {

    override suspend fun getCurrentUser(): NetworkResult<FirebaseUser?> {
        return try {
            NetworkResult.Success(firebaseAuth.currentUser)
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
        }
    }

    override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                if (result.user!!.isEmailVerified) {
                    NetworkResult.Success(result.user!!)
                } else {
                    NetworkResult.Failure(UiText.StringResource(R.string.verify_email))
                }
            } else {
                NetworkResult.Failure(UiText.StringResource(R.string.user_is_null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
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
                NetworkResult.Failure(UiText.StringResource(R.string.failed_google_sign))
            }
        } catch (e: Exception) {
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
        }
    }

    override suspend fun facebookSignIn(token: AccessToken): NetworkResult<FirebaseUser?> {
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
            NetworkResult.Success(result.user)
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
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
                NetworkResult.Failure(UiText.StringResource(R.string.user_is_null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): NetworkResult<Boolean> {
        return try {
            val isEmailPasswordProvider =
                firebaseAuth.currentUser?.providerData?.any { it.providerId == "password" } ?: false

            if (isEmailPasswordProvider) {
                val result = firebaseAuth.sendPasswordResetEmail(email)
                result.await()

                if (result.isSuccessful) {
                    NetworkResult.Success(true)
                } else {
                    NetworkResult.Failure(UiText.StringResource(R.string.failed_password_reset))
                }
            } else {
                NetworkResult.Failure(UiText.StringResource(R.string.incorrect_provider))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
        }
    }

    override suspend fun logout(): NetworkResult<Boolean> {
        return try {
            firebaseAuth.signOut()
            NetworkResult.Success(true)
        } catch (e: Exception) {
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
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
        val fullName = user?.displayName ?: ""
        val email = user?.email ?: ""
        val uid = user?.uid ?: ""
        val newUser = User(fullName, email, Date(), uid)

        users.document(uid).set(newUser).await()
    }
    // endregion

}