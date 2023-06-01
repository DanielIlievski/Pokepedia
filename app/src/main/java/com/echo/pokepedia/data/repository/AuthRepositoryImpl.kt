package com.echo.pokepedia.data.repository

import android.util.Log
import com.echo.pokepedia.R
import com.echo.pokepedia.data.database.room.authentication.LocalAuthenticationDataSourceImpl
import com.echo.pokepedia.data.mappers.toUser
import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.domain.authentication.model.database.UserEntity
import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.USERS_COLLECTION
import com.echo.pokepedia.util.UiText
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val localAuthenticationDataSourceImpl: LocalAuthenticationDataSourceImpl,
    @Named(USERS_COLLECTION) private val users: CollectionReference
) : AuthRepository {

    override suspend fun getCurrentUser(): NetworkResult<User> {
        return try {
            val currentUser = localAuthenticationDataSourceImpl.getUser()
            NetworkResult.Success(currentUser)
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
                    addUserToDatabase(result!!.user?.toUser())
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
                val result = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    addUserToFirestore(firebaseUser.toUser())
                    addUserToDatabase(firebaseUser.toUser())
                    NetworkResult.Success(firebaseUser)
                } else {
                    NetworkResult.Failure(UiText.StringResource(R.string.user_is_null))
                }
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
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                addUserToFirestore(firebaseUser.toUser())
                addUserToDatabase(firebaseUser.toUser())
                NetworkResult.Success(firebaseUser)
            } else {
                NetworkResult.Failure(UiText.StringResource(R.string.user_is_null))
            }
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
                addUserToFirestore(user.toUser())
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
            localAuthenticationDataSourceImpl.deleteUser()
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

    private suspend fun addUserToFirestore(user: User) {
        users.document(user.firebaseId!!).set(user).await()
    }

    private suspend fun addUserToDatabase(user: User?) {
        try {
            val userDocument = users.document(user!!.firebaseId ?: "").get().await().data
            val userEntity = UserEntity(
                fullName = userDocument?.get("fullName").toString(),
                email = userDocument?.get("email").toString(),
                profilePicture = userDocument?.get("profilePicture").toString(),
                firebaseId = userDocument?.get("firebaseId").toString(),
                date = (userDocument?.get("date") as Timestamp).toDate(),
            )
            Log.d("HelloWorld", "addUserToDatabase: $userEntity")
            localAuthenticationDataSourceImpl.insertUser(userEntity.toUser())

        } catch (e: Exception) {
            Log.d("HelloWorld", "addUserToDatabase: ${e.message}")
        }
    }
    // endregion

}