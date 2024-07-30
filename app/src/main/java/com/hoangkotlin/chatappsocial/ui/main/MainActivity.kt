package com.hoangkotlin.chatappsocial.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.bubble_service.OverlayBubbleService
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.core.ui.utils.LocalSocketState
import com.hoangkotlin.chatappsocial.feature.home.navigation.CHANNELS_GRAPH_ROUTE_PATTERN
import com.hoangkotlin.chatappsocial.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var mService: OverlayBubbleService
    private var mBound: Boolean = false
    private lateinit var activityPermissionResultLauncher: ActivityResultLauncher<Intent>

    /** Defines callbacks for service binding, passed to bindService().  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as OverlayBubbleService.LocalBinder
            mService = binder.getService()
            mBound = true
            if (!mService.isBubbleRunning){
                mService.showOverlay(this@MainActivity)
                moveTaskToBack(true)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerForPermissionResult()

        setContent {
            SocialChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val socketState by viewModel.socketState.collectAsStateWithLifecycle()
                    CompositionLocalProvider(
                        LocalSocketState provides socketState
                    ) {
                        val currentUser
                                by viewModel.currentUser.collectAsStateWithLifecycle()

                        return@CompositionLocalProvider SocialApp(
                            startDestination = CHANNELS_GRAPH_ROUTE_PATTERN,
                            currentUser = currentUser,
                            onLogOutClick = {
                                viewModel.logOut()
                                val authIntent = Intent(this@MainActivity, AuthActivity::class.java)
                                startActivity(authIntent)
                                finish()
                            },
                            onToggleBubbleClicked = ::openBubbleForChannel
                        )
                    }


                }


            }
        }

    }

    private fun registerForPermissionResult() {
        activityPermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (Settings.canDrawOverlays(this) && mBound) {
                mService.showOverlay(this@MainActivity)
            }
        }
    }

    private fun openBubbleForChannel(channelId: String) {
        checkOverlayPermission()
        if (mBound.not()) {
            val bubbleService = Intent(this@MainActivity, OverlayBubbleService::class.java)
            startService(bubbleService.also { service ->
                bindService(service, connection, Context.BIND_AUTO_CREATE)
            })
        }
//        if (checkOverlayPermission() && mBound) {
//            mService.showOverlay(this@MainActivity)
//            moveTaskToBack(true)
//        }
    }

    private fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this).also { hasPermission ->
            if (!hasPermission) {
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                activityPermissionResultLauncher.launch(myIntent)
            }
        }
    }


    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(connection)
            mBound = false
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}

