package com.echo.pokepedia.data.repository

import android.net.Uri
import android.util.Log
import com.echo.pokepedia.R
import com.echo.pokepedia.data.database.LocalAuthenticationDataSource
import com.echo.pokepedia.data.mappers.toUser
import com.echo.pokepedia.domain.authentication.model.User
import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.util.USERS_COLLECTION
import com.echo.pokepedia.util.UiText
import com.echo.pokepedia.util.getUsername
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val localAuthenticationDataSource: LocalAuthenticationDataSource,
    private val firebaseStorageReference: StorageReference,
    @Named(USERS_COLLECTION) private val users: CollectionReference
) : AuthRepository {

    override suspend fun getCurrentUser(): NetworkResult<Flow<User>> {
        return try {
            NetworkResult.Success(localAuthenticationDataSource.getUser())
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
        }
    }

    override fun isUserAuthenticated(): Boolean {
        return if (firebaseAuth.currentUser != null) {
            if (isEmailPasswordProvider() && firebaseAuth.currentUser!!.isEmailVerified) {
                true
            } else !isEmailPasswordProvider()
        } else {
            false
        }
    }

    override suspend fun updateUserProfilePhoto(imgUri: Uri?): NetworkResult<Boolean> {
        return try {
            if (imgUri != null) {
                uploadPhotoToFirebaseStorage(imgUri)
                val storageImg = getProfilePhotoFromFirebaseStorage()
                updateProfilePhotoInFirestoreAndDb(storageImg)
            } else {
                deletePhotoFromFirebaseStorage()
                updateProfilePhotoInFirestoreAndDb("")
            }
            NetworkResult.Success(true)
        } catch (e: Exception) {
            NetworkResult.Failure(UiText.DynamicString(e.localizedMessage))
        }
    }

    override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                if (result.user!!.isEmailVerified) {
                    addUserToDatabase(result.user!!)
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
                val isUserNew = !users.document(firebaseUser?.uid ?: "").get().await().exists()
                if (firebaseUser != null) {
                    if (isUserNew) {
                        addUserToFirestore(firebaseUser.toUser())
                    }
                    addUserToDatabase(firebaseUser)
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
            val isUserNew = !users.document(firebaseUser?.uid ?: "").get().await().exists()
            if (firebaseUser != null) {
                if (isUserNew) {
                    addUserToFirestore(firebaseUser.toUser())
                }
                addUserToDatabase(firebaseUser)
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
            if (isEmailPasswordProvider()) {
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
            localAuthenticationDataSource.deleteUser()
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
        val username = getUsername(firstName, lastName)
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        result?.user?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(username).build()
        )?.await()
        return result.user
    }

    private suspend fun addUserToFirestore(user: User) {
        try {
            users.document(user.firebaseId!!).set(user).await()
        } catch (e: Exception) {
            Log.d("HelloWorld", "addUserToFirestore: ${e.message}")
            throw e
        }
    }

    private suspend fun addUserToDatabase(firebaseUser: FirebaseUser) {
        try {
            val user = users.document(firebaseUser.uid).get().await().toObject(User::class.java)
            if (user != null) {
                localAuthenticationDataSource.insertUser(user)
            }
        } catch (e: Exception) {
            Log.d("HelloWorld", "addUserToDatabase: ${e.message}")
            throw e
        }
    }

    private suspend fun uploadPhotoToFirebaseStorage(imgUri: Uri) {
        try {
            storageRef().putFile(imgUri).await()
        } catch (e: Exception) {
            Log.d("HelloWorld", "uploadPhotoToFirebaseStorage: ${e.localizedMessage}")
            throw e
        }
    }

    private suspend fun deletePhotoFromFirebaseStorage() {
        try {
            storageRef().delete().await()
        } catch (e: Exception) {
            Log.d("HelloWorld", "deletePhotoFromFirebaseStorage: ${e.localizedMessage}")
            throw e
        }
    }

    private suspend fun getProfilePhotoFromFirebaseStorage(): String {
        val imgUri = storageRef().downloadUrl.await()
        return imgUri.toString()
    }

    private suspend fun updateProfilePhotoInFirestoreAndDb(imgUrl: String) {
        try {
            val user = localAuthenticationDataSource.getUser().firstOrNull()
                ?.copy(profilePicture = imgUrl)
            if (user != null) {
                addUserToFirestore(user)
                localAuthenticationDataSource.updateProfilePhoto(user)
            }
        } catch (e: Exception) {
            Log.d("HelloWorld", "updateProfilePhotoInFirestoreAndDb: ${e.localizedMessage}")
            throw e
        }
    }

    private fun storageRef(): StorageReference {
        val userId = firebaseAuth.currentUser?.uid
        return firebaseStorageReference.child("$userId/profile_photo/$userId")
    }

    private fun isEmailPasswordProvider(): Boolean {
        return firebaseAuth.currentUser?.providerData?.any { it.providerId == "password" } ?: false
    }
    // endregion

}