package com.example.easysell.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesRepository(
    private val context: Context
) {

    companion object {
        private val ORDER_NAME_KEY =
            stringPreferencesKey("order_name")

        private const val DEFAULT_ORDER_NAME =
            "Objednávka"
    }

    val orderName: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[ORDER_NAME_KEY]
                ?: DEFAULT_ORDER_NAME
        }

    suspend fun setOrderName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[ORDER_NAME_KEY] = name
        }
    }
}