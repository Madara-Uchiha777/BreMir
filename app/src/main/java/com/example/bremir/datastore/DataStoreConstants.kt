package com.example.bremir.datastore

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreConstants {
    val EMAIL_KEY = stringPreferencesKey("EMAIL_KEY")
    val PASSWORD_KEY = stringPreferencesKey("PASSWORD_KEY")
    val USER_KEY = stringPreferencesKey("USER_KEY")
    val LANGUAGE_KEY = stringPreferencesKey("LANGUAGE_KEY")
}