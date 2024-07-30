package com.hoangkotlin.chatappsocial.bubble_service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.hoangkotlin.chatappsocial.ui.bubble.BubbleFloatingView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "OverlayBubbleServiceTes"

class OverlayBubbleServiceTest : Service() {

    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        showOverlay()
    }


    private fun showOverlay() {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        val params = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            layoutFlag,
//             WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            PixelFormat.TRANSLUCENT
        )
        val startLocation: Point = Point(0, 0)
        params.apply {
            type =
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

            gravity = Gravity.TOP or Gravity.START
            format = PixelFormat.TRANSLUCENT
            alpha = 1f
            // danger, these may ignore match_parent
//            if (isFillMaxWidth.not()) {
//            width = WindowManager.LayoutParams.WRAP_CONTENT
//            }
            height = WindowManager.LayoutParams.WRAP_CONTENT

            flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND or
                    WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER

            x = startLocation.x
            y = startLocation.y

//            expandedBubbleStyle?.let {
//                windowAnimations = it
//            }

            dimAmount = 0.5f

            type =
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }

        val composeView = ComposeView(this)
        composeView.setContent {
            var prevX by remember {
                mutableIntStateOf(0)
            }
            var prevY by remember {
                mutableIntStateOf(0)
            }
            BubbleFloatingView(
                onIsCollapsedChanged = { isCollapsed ->
                    if (!isCollapsed) {
                        params.apply {
                            x = 0
                            y = 0
                            width = WindowManager.LayoutParams.MATCH_PARENT
                            flags = flags.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                                .rem(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                        }

                    } else {
                        params.apply {
                            x = prevX
                            y = prevY
                            flags = flags.rem(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                                .or(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                            width = WindowManager.LayoutParams.WRAP_CONTENT
                        }
                    }

                    windowManager.updateViewLayout(composeView, params)

                },
                onDrag = { offsetX, offsetY ->
                    Log.d(TAG, "showOverlay: x: $offsetX")
                    Log.d(TAG, "showOverlay: y: $offsetY")
                    val screenWidth = windowManager.currentWindowMetrics.bounds.width()
                    val screenHeight = windowManager.currentWindowMetrics.bounds.height()
                    params.apply {
                        val newX = offsetX.toInt()
                        val newY = offsetY.toInt()

//                        if (offsetY < 0) {
//                            newY = 0
//                        } else if (offsetY > screenHeight) {
//                            newY = screenHeight
//                        }
//
//                        if (offsetX < 0) {
//                            newX = 0
//                        } else if (offsetX > screenWidth) {
//                            newX = screenWidth
//                        }
//                        Log.d(TAG, "showOverlay: newX: $newX")
//                        Log.d(TAG, "showOverlay: newY: $newY")
                        x = newX
//                            .coerceIn(0, screenWidth - 200)
                        y = newY
//                            .coerceIn(0, screenHeight - 500)
                        Log.d(TAG, "showOverlay: newX: $x")
                        Log.d(TAG, "showOverlay: newY: $y")
                        prevX = x
                        prevY = y

                    }
                    windowManager.updateViewLayout(composeView, params)
                }
            )

        }


        // Trick The ComposeView into thinking we are tracking lifecycle
        val viewModelStore = ViewModelStore()
        val lifecycleOwner = SocialBubbleLifecycleOwner()

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        })
//        composeView.setViewTreeOnBackPressedDispatcherOwner(object : OnBackPressedDispatcherOwner {
//            override val lifecycle: Lifecycle
//                get() = lifecycleOwner.lifecycle
//            override val onBackPressedDispatcher: OnBackPressedDispatcher
//                get() = OnBackPressedDispatcher(null)
//
//        })

        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val recompose = Recomposer(coroutineContext)
        composeView.compositionContext = recompose
        runRecomposeScope.launch {
            recompose.runRecomposeAndApplyChanges()
        }

        windowManager.addView(composeView, params)
    }
}