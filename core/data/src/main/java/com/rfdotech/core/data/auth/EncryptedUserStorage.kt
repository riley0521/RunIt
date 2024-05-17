package com.rfdotech.core.data.auth

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.auth.UserStorage
import kotlinx.coroutines.withContext

class EncryptedUserStorage(
    private val sharedPreferences: SharedPreferences,
    private val dispatcherProvider: DispatcherProvider
) : UserStorage {

    @SuppressLint("ApplySharedPref")
    override suspend fun set(userId: UserId?) {
        return withContext(dispatcherProvider.io) {
            if (userId == null) {
                sharedPreferences.edit().remove(USER_ID_KEY).commit()
                return@withContext
            }

            sharedPreferences.edit().putString(USER_ID_KEY, userId).commit()
        }
    }

    override suspend fun get(): UserId? {
        return withContext(dispatcherProvider.io) {
            sharedPreferences.getString(USER_ID_KEY, null)
        }
    }

    companion object {
        private const val USER_ID_KEY = "USER_ID_KEY"
    }
}