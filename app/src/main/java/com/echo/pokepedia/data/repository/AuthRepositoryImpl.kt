package com.echo.pokepedia.data.repository

import com.echo.pokepedia.R
import com.echo.pokepedia.data.model.User
import com.echo.pokepedia.util.NetworkResult
import com.echo.pokepedia.domain.repository.AuthRepository
import com.echo.pokepedia.util.ResourceProvider
import com.echo.pokepedia.util.USERS_COLLECTION
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val resourceProvider: ResourceProvider,
    @Named(USERS_COLLECTION) private val users: CollectionReference
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
            if (firebaseAuth.currentUser!!.isEmailVerified) {
                NetworkResult.Success(result.user!!)
            } else {
                NetworkResult.Failure(Exception(resourceProvider.fetchString(R.string.verify_email)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

            addUserToFirestore(firstName, lastName, email)

            firebaseAuth.currentUser?.sendEmailVerification()

            NetworkResult.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Failure(e)
        }
    }

    private suspend fun createNewUser(firstName: String, lastName: String, email: String, password: String) : FirebaseUser {
        val username = firstName + lastName
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        result?.user?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(username).build()
        )?.await()
        return result.user!!
    }

    private suspend fun addUserToFirestore(firstName: String, lastName: String, email: String) {
        val newUser = User(firstName, lastName, email, Timestamp.now())
        users.add(newUser).await()
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

}