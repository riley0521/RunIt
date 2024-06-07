package com.rfdotech.runtracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space8
import com.rfdotech.core.presentation.ui.showToastRes
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainViewModel>()
    private val auth by lazy {
        Firebase.auth
    }

    private lateinit var splitInstallManager: SplitInstallManager
    private val splitInstallListener = SplitInstallStateUpdatedListener { state ->
        when (state.status()) {
            SplitInstallSessionStatus.INSTALLED -> {
                viewModel.setAnalyticsDialogVisibility(false)
                this@MainActivity.showToastRes(R.string.analytics_installed)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                viewModel.setAnalyticsDialogVisibility(true)
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                viewModel.setAnalyticsDialogVisibility(true)
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                splitInstallManager.startConfirmationDialogForResult(state, this, 0)
            }
            SplitInstallSessionStatus.FAILED -> {
                viewModel.setAnalyticsDialogVisibility(false)
                this@MainActivity.showToastRes(R.string.error_installation_failed)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.state.isCheckingAuth
            }
        }
        splitInstallManager = SplitInstallManagerFactory.create(applicationContext)

        setContent {
            RunItTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ObserveAuthenticationState()

                    with(viewModel.state) {
                        if (!isCheckingAuth) {
                            val navController = rememberNavController()
                            NavigationRoot(
                                navController = navController,
                                isSignedIn = isSignedIn,
                                onAnalyticsClick = {
                                    installOrStartAnalyticsFeature()
                                }
                            )

                            if (showAnalyticsInstallDialog) {
                                Dialog(onDismissRequest = {}) {
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(Space16))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(Space16),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(Space8))
                                        Text(
                                            text = stringResource(id = R.string.analytics_installing),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ObserveAuthenticationState() {
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            val authStateListener = AuthStateListener { state ->
                if (state.currentUser == null && viewModel.state.isSignedIn) {
                    this@MainActivity.showToastRes(R.string.session_expired_sign_in_again)
                    viewModel.onAction(MainAction.OnAuthenticationExpired)
                }
            }
            val lifecycleListener = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    auth.addAuthStateListener(authStateListener)
                } else if (event == Lifecycle.Event.ON_STOP) {
                    auth.removeAuthStateListener(authStateListener)
                }
            }
            lifecycle.addObserver(lifecycleListener)

            onDispose {
                lifecycle.removeObserver(lifecycleListener)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(splitInstallListener)
    }

    override fun onPause() {
        super.onPause()
        splitInstallManager.unregisterListener(splitInstallListener)
    }

    private fun installOrStartAnalyticsFeature() {
        if (splitInstallManager.installedModules.contains("analytics_feature")) {
            Intent()
                .setClassName(packageName, "com.rfdotech.analytics.analytics_feature.AnalyticsActivity")
                .also(::startActivity)
            return
        }

        val request = SplitInstallRequest.newBuilder()
            .addModule("analytics_feature")
            .build()

        splitInstallManager.startInstall(request).addOnFailureListener {
            it.printStackTrace()
            this@MainActivity.showToastRes(R.string.error_load_module)
        }
    }
}