package com.echo.pokepedia.data.preferences

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val LAYOUT_PREFERENCES_NAME = "layout_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCES_NAME
)

class SettingsDataStore @Inject constructor(
    @ApplicationContext val context: Context
) {

    // region IS_ONBOARDING_AVAILABLE
    private val IS_ONBOARDING_AVAILABLE = booleanPreferencesKey("is_onboarding_available")

    suspend fun saveOnBoardingToPreferencesStore(isOnBoardingAvailable: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_AVAILABLE] = isOnBoardingAvailable
        }
    }

    val onBoardingPreferencesFlow: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_ONBOARDING_AVAILABLE] ?: true
        }
    // endregion

    // region BUDDY_POKEMON_NAME
    private val BUDDY_POKEMON_NAME = stringPreferencesKey("buddy_pokemon_name")

    suspend fun saveBuddyPokemonIdToPreferencesStore(buddyPokemonName: String) {
        context.dataStore.edit { preferences ->
            preferences[BUDDY_POKEMON_NAME] = buddyPokemonName
        }
    }

    val buddyPokemonNameFlow: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            Log.d("HelloWorld45", ":${preferences[BUDDY_POKEMON_NAME]} ")
            preferences[BUDDY_POKEMON_NAME] ?: ""
        }
    // endregion

    // region BUDDY_POKEMON_NICKNAME
    private val BUDDY_POKEMON_NICKNAME = stringPreferencesKey("buddy_pokemon_nickname")

    suspend fun saveBuddyPokemonNicknameToPreferencesStore(buddyPokemonNickname: String) {
        context.dataStore.edit { preferences ->
            preferences[BUDDY_POKEMON_NICKNAME] = buddyPokemonNickname
        }
    }

    val buddyPokemonNicknameFlow: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[BUDDY_POKEMON_NICKNAME] ?: ""
        }
    // endregion

    // region BUDDY_POKEMON_DOMINANT_COLOR
    private val BUDDY_POKEMON_DOMINANT_COLOR = intPreferencesKey("buddy_pokemon_dominant_color")

    suspend fun saveBuddyPokemonDominantColorToPreferencesStore(pokemonDominantColor: Int) {
        context.dataStore.edit {preferences ->
            preferences[BUDDY_POKEMON_DOMINANT_COLOR] = pokemonDominantColor
        }
    }

    val buddyPokemonDominantColorFlow: Flow<Int> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[BUDDY_POKEMON_DOMINANT_COLOR] ?: Color.WHITE
        }
    // endregion
}