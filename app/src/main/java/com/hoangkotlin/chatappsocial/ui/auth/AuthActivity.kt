package com.hoangkotlin.chatappsocial.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.ui.main.MainActivity
import com.hoangkotlin.chatappsocial.ui.main.MainActivityUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    private val viewModel: AuthActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val splashScreen = installSplashScreen()

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.onEach { state ->
                    uiState = state
                    Log.d("AuthActivity", "onEach: $state")
                }.collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                else -> false
            }
        }

        setContent {
            SocialChatAppTheme {
                // A surface container using the 'background' color from the theme

                ObserveAsEvents(flow = viewModel.navigationEvent) { event ->
                    Log.d("ObserveAsEvents", "onEvent: $event")
                    when (event) {
                        NavigationEvent.NavigateToHome -> navigateToMainActivity()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    when (uiState) {
                        is MainActivityUiState.AuthFailed -> {
                            AuthNavHost(
                                navController = rememberNavController(),
                                onNavigateToHome = {

                                })
                        }

                        is MainActivityUiState.AuthSuccess -> {}

                        MainActivityUiState.Loading -> {}
                    }
                }
            }
        }
    }


    private fun navigateToMainActivity() {
        val mainIntent = Intent(this@AuthActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
        onDestroy()

    }

    @Composable
    fun <T> ObserveAsEvents(flow: Flow<T>, onEvent: (T) -> Unit) {
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(flow, lifecycleOwner) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(onEvent)
            }
        }
    }
}

