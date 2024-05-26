package com.rfdotech.core.data.auth

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.rfdotech.core.domain.AuthInfo
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncryptedSessionStorage(
    private val sharedPreferences: SharedPreferences,
    private val dispatcherProvider: DispatcherProvider
) : SessionStorage {
    override suspend fun get(): AuthInfo? {
        return withContext(dispatcherProvider.io) {
            val json = sharedPreferences.getString(KEY_AUTH_INFO, null)
            json?.let {
                val authInfoSerializable = Json.decodeFromString<AuthInfoSerializable>(json)
                authInfoSerializable.toAuthInfo()
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun set(info: AuthInfo?) {
        withContext(dispatcherProvider.io) {
            if(info == null) {
                sharedPreferences.edit().remove(KEY_AUTH_INFO).commit()
                return@withContext
            }

            val json = Json.encodeToString(info.toAuthInfoSerializable())

            sharedPreferences
                .edit()
                .putString(KEY_AUTH_INFO, json)
                .commit()
        }
    }

    companion object {
        private const val KEY_AUTH_INFO = "KEY_AUTH_INFO"
    }
}