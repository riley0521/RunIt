package com.rfdotech.analytics.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.analytics.presentation.AnalyticsSharedViewModel
import com.rfdotech.core.presentation.designsystem.RunItTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDetailScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AnalyticsSharedViewModel = koinViewModel()
) {
    AnalyticsDetailScreen(
        state = viewModel.detailState,
        onAction = { action ->
            when (action) {
                AnalyticsDetailAction.OnBackClick -> {
                    onBackClick()
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
private fun AnalyticsDetailScreen(
    state: AnalyticsDetailState,
    onAction: (AnalyticsDetailAction) -> Unit
) {

}

@Preview
@Composable
private fun AnalyticsDetailScreenPreview() {
    RunItTheme {
        AnalyticsDetailScreen(
            state = AnalyticsDetailState(),
            onAction = {}
        )
    }
}
