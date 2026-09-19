package com.difome.fast.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.difome.fast.common.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings_prefs")

class SettingsPreferences(private val context: Context) {

    companion object {
        val KEY_IS_FAHRENHEIT = booleanPreferencesKey("is_fahrenheit")
        val KEY_WIND_UNIT = stringPreferencesKey("wind_unit")
        val KEY_PRESSURE_UNIT = stringPreferencesKey("pressure_unit")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }

    val isFahrenheit: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[KEY_IS_FAHRENHEIT] ?: false
    }

    val windUnit: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[KEY_WIND_UNIT] ?: AppConstants.WIND_UNIT_MS
    }

    val pressureUnit: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[KEY_PRESSURE_UNIT] ?: AppConstants.PRESSURE_UNIT_MBAR
    }

    val themeMode: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: AppConstants.THEME_SYSTEM
    }

    val isDynamicColor: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[KEY_DYNAMIC_COLOR] ?: true
    }

    suspend fun saveFahrenheit(isFahrenheit: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_IS_FAHRENHEIT] = isFahrenheit
        }
    }

    suspend fun saveWindUnit(unit: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_WIND_UNIT] = unit
        }
    }

    suspend fun savePressureUnit(unit: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_PRESSURE_UNIT] = unit
        }
    }

    suspend fun saveThemeMode(mode: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    suspend fun saveDynamicColor(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_DYNAMIC_COLOR] = enabled
        }
    }
}
