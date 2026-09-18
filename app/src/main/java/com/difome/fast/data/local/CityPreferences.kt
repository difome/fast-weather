package com.difome.fast.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.difome.fast.common.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class CityPreferences(private val context: Context) {

    companion object {
        private val KEY_SELECTED_CITY = stringPreferencesKey("selected_city_id")
    }

    val selectedCityId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SELECTED_CITY] ?: AppConstants.defaultCityId
    }

    // 2. Функция для СОХРАНЕНИЯ города
    suspend fun saveSelectedCity(cityId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SELECTED_CITY] = cityId
        }
    }
}