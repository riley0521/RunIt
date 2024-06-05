package com.rfdotech.run.presentation.active_run

import com.rfdotech.run.domain.StepObserver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeStepObserver : StepObserver {

    private val _stepCount = Channel<Int>()
    private var currentSteps = 0

    suspend fun incrementStep() {
        currentSteps += 1
        _stepCount.send(currentSteps)
    }

    override fun observeSteps(currentSteps: Int): Flow<Int> {
        this.currentSteps = currentSteps
        return _stepCount.receiveAsFlow()
    }
}