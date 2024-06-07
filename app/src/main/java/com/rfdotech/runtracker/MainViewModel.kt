package com.rfdotech.runtracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.domain.auth.UserStorage
import kotlinx.coroutines.launch

class MainViewModel(
    private val userStorage: UserStorage
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            state = state.copy(isCheckingAuth = true)

            state = state.copy(isSignedIn = userStorage.get() != null)

            state = state.copy(isCheckingAuth = false)
        }
    }

    fun onAction(action: MainAction) {
        when (action) {
            MainAction.OnAuthenticationExpired -> {
                state = state.copy(isSignedIn = false)
            }
        }
    }

    fun setAnalyticsDialogVisibility(isVisible: Boolean) {
        state = state.copy(showAnalyticsInstallDialog = isVisible)
    }
}