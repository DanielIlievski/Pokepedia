package com.echo.pokepedia.di

import com.echo.pokepedia.data.network.RemotePokemonDataSource
import com.echo.pokepedia.data.network.retrofit.PokemonDbApi
import com.echo.pokepedia.data.network.retrofit.RetrofitPokemonDataSource
import com.echo.pokepedia.data.repository.AuthRepositoryImpl
import com.echo.pokepedia.data.repository.PokemonRepositoryImpl
import com.echo.pokepedia.domain.authentication.repository.AuthRepository
import com.echo.pokepedia.domain.pokemon.repository.PokemonRepository
import com.echo.pokepedia.util.BASE_URL
import com.echo.pokepedia.util.USERS_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)

    @Singleton
    @Provides
    @Named(USERS_COLLECTION)
    fun provideUsersCollection(db: FirebaseFirestore): CollectionReference =
        db.collection(USERS_COLLECTION)

    @Singleton
    @Provides
    fun providePokemonRepository(impl: PokemonRepositoryImpl): PokemonRepository = impl

    @Singleton
    @Provides
    fun providePokemonDbApi(): PokemonDbApi {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp)
            .build()
            .create(PokemonDbApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRemotePokemonDataSource(retrofitPokemonDataSource: RetrofitPokemonDataSource):
            RemotePokemonDataSource = retrofitPokemonDataSource
}