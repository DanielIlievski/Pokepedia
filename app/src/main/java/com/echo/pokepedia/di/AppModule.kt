package com.echo.pokepedia.di

import com.echo.pokepedia.domain.repository.AuthRepository
import com.echo.pokepedia.data.repository.AuthRepositoryImpl
import com.echo.pokepedia.util.USERS_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Named(USERS_COLLECTION)
    fun provideUsersCollection(db: FirebaseFirestore): CollectionReference =
        db.collection(USERS_COLLECTION)
}