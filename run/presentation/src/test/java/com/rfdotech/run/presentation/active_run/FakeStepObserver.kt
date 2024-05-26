package com.rfdotech.run.presentation.active_run

import com.rfdotech.run.domain.StepObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeStepObserver : StepObserver {

    override fun observeSteps(currentSteps: Int): Flow<Int> {
        return flow {
            var step = currentSteps
            while (true) {
                delay(1_000)

                step += 1
                emit(step)
            }
        }
    }
}