package com.echo.pokepedia.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.echo.pokepedia.data.database.LocalPokemonDataSource
import com.echo.pokepedia.data.database.room.*
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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

    @Provides
    @Singleton
    fun provideGlideInstance(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context)
    }

    @Singleton
    @Provides
    fun provideRemotePokemonDataSource(retrofitPokemonDataSource: RetrofitPokemonDataSource):
            RemotePokemonDataSource = retrofitPokemonDataSource

    @Singleton
    @Provides
    fun provideLocalPokemonDataSource(roomPokemonDataSource: RoomPokemonDataSource):
            LocalPokemonDataSource = roomPokemonDataSource

    @Singleton
    @Provides
    fun providePokemonDao(database: PokepediaDatabase): PokemonDao = database.pokemonDao()

    @Singleton
    @Provides
    fun provideStatDao(database: PokepediaDatabase): StatDao = database.statDao()

    @Singleton
    @Provides
    fun provideTeamMemberDao(database: PokepediaDatabase): TeamMemberDao = database.teamMemberDao()

    @Singleton
    @Provides
    fun providePokepediaDb(@ApplicationContext context: Context): PokepediaDatabase =
        Room.databaseBuilder(
            context,
            PokepediaDatabase::class.java,
            "pokepedia_db"
        ).build()

    @Singleton
    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO
}