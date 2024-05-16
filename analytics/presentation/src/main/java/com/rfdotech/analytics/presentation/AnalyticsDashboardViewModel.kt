package com.rfdotech.analytics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.analytics.domain.AnalyticsRepository
import com.rfdotech.analytics.presentation.mapper.toAnalyticsDashboardState
import kotlinx.coroutines.launch

class AnalyticsDashboardViewModel(
    private val analyticsRepository: AnalyticsRepository
): ViewModel() {

    var state by mutableStateOf(AnalyticsDashboardState())
        private set

    init {
        viewModelScope.launch {
            val analyticsValues = analyticsRepository.getAnalyticsValues()
            state = analyticsValues.toAnalyticsDashboardState()
        }
    }
}