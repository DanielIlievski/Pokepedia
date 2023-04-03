package com.echo.pokepedia.data.repository

import android.content.res.Resources
import androidx.core.content.ContextCompat
import com.echo.pokepedia.R
import com.echo.pokepedia.util.Resource
import com.echo.pokepedia.domain.repository.AuthRepository
import com.echo.pokepedia.util.ResourceProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val resourceProvider: ResourceProvider
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

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val username = firstName + lastName
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(username).build()
            )?.await()
            firebaseAuth.currentUser?.sendEmailVerification()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

}