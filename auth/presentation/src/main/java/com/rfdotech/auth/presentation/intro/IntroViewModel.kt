package com.rfdotech.auth.presentation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.auth.UserStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class IntroViewModel(
    private val userStorage: UserStorage
) : ViewModel() {

    private val eventChannel = Channel<IntroEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onSignInSuccess(userId: UserId) = viewModelScope.launch {
        userStorage.set(userId)
        eventChannel.send(IntroEvent.OnSignInSuccessful)
    }
}