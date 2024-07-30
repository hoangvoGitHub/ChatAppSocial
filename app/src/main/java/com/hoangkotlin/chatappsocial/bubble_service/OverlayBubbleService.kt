package com.hoangkotlin.chatappsocial.bubble_service

import android.animation.ObjectAnimator
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PointF
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.hoangkotlin.chatappsocial.bubble_service.utils.getCircleBound
import com.hoangkotlin.chatappsocial.bubble_service.utils.getPosition
import com.hoangkotlin.chatappsocial.bubble_service.utils.isCollidingWith
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.core.ui.utils.LocalRunningForBubble
import com.hoangkotlin.chatappsocial.ui.bubble.BubbleAlignment
import com.hoangkotlin.chatappsocial.ui.bubble.FloatingCloseBubble
import com.hoangkotlin.chatappsocial.ui.bubble.FloatingViewMain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "OverlayBubbleService"

@AndroidEntryPoint
internal class OverlayBubbleService : Service() {

    private val windowManager by lazy {
        getSystemService(WINDOW_SERVICE) as WindowManager
    }

    private lateinit var mainView: ComposeView
    private lateinit var mainViewParams: WindowManager.LayoutParams

    private lateinit var closeView: ComposeView
    private lateinit var closeViewParams: WindowManager.LayoutParams

    private var bubbleAlignment by mutableStateOf(BubbleAlignment.Start)
    private var shouldZoomCloseView by mutableStateOf(false)
    private val previousPosition = PointF(0f, 0f)

    private var isCloseViewShowing = false
    private var isCloseViewAnimating = false
    private var springCloseAnim: SpringAnimation? = null

    var isBubbleRunning = false
        private set

    private val binder = LocalBinder()

    @Inject
    lateinit var chatClient: ChatClient

    inner class LocalBinder : Binder() {
        fun getService(): OverlayBubbleService = this@OverlayBubbleService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun showOverlay(context: Context) {
        isBubbleRunning = true
        prepareMainView(context)
        prepareCloseView(context)
    }


    private fun prepareMainView(context: Context) {
        mainViewParams = WindowManager.LayoutParams()
        val startLocation = Point(-1000, 0)
        mainViewParams.apply {
            type =
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

            gravity = Gravity.TOP or Gravity.START
            format = PixelFormat.TRANSLUCENT
            alpha = 1f

            height = WindowManager.LayoutParams.WRAP_CONTENT

            flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND or
                    WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER

            x = startLocation.x
            y = startLocation.y

            dimAmount = 0.5f

            type =
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                setCanPlayMoveAnimation(true)
            }
        }
        mainView = ComposeView(context)
        mainView.setContent {
            SocialChatAppTheme {

                val currentUser by chatClient.clientState.user.collectAsStateWithLifecycle()

                CompositionLocalProvider(LocalRunningForBubble provides true) {
                    FloatingViewMain(
                        currentUser = currentUser,
                        onIsCollapsedChanged = { isCollapsed ->
                            onIsCollapsedChanged(isCollapsed)
                            updatePreviousPosition()
                        },
                        bubbleAlignment = bubbleAlignment,
                        onDrag = { offsetX, offsetY ->
                            onPositionChanged(offsetX, offsetY)
                            animateShowCloseView()
                        },
                        onDragEnd = {
                            onMainViewDragEnd(
                                onAnimationEnd = {
                                    bubbleAlignment = it
                                    updatePreviousPosition()
                                })
                        },
                        previousPosition = previousPosition
                    )
                }

            }
        }

        // Trick The ComposeView into thinking we are tracking lifecycle
        val viewModelStore = ViewModelStore()
        val lifecycleOwner = SocialBubbleLifecycleOwner()

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        mainView.setViewTreeLifecycleOwner(lifecycleOwner)
        mainView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        mainView.setViewTreeViewModelStoreOwner(SocialViewModelStoreOwner(viewModelStore))

        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val recompose = Recomposer(coroutineContext)
        mainView.compositionContext = recompose
        runRecomposeScope.launch {
            recompose.runRecomposeAndApplyChanges()
        }
        windowManager.addView(mainView, mainViewParams)
        val animator = ObjectAnimator.ofFloat(mainView, "x", -1000f, 0f)
        animator.duration = 500  // Duration in milliseconds
        animator.start()
    }

    private fun prepareCloseView(context: Context) {
        closeView = ComposeView(context)

        closeViewParams = WindowManager.LayoutParams().apply {
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL

            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT

            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
//            y = screenHeight + 1000
            y = -500
            format = PixelFormat.TRANSLUCENT

            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }

        closeView.setContent {
            FloatingCloseBubble(shouldZoomCloseView = shouldZoomCloseView)
        }
        val closeViewLifecycleOwner = SocialBubbleLifecycleOwner()
        closeViewLifecycleOwner.performRestore(null)
        closeViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        closeViewLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        closeView.setViewTreeLifecycleOwner(closeViewLifecycleOwner)
        closeView.setViewTreeSavedStateRegistryOwner(closeViewLifecycleOwner)

        windowManager.addView(closeView, closeViewParams)
    }


    private fun animateShowCloseView() {
        if (isCloseViewShowing || isCloseViewAnimating) return
        springCloseAnim?.cancel()

        val bubbleWidth = closeView.width
        val startY = closeViewParams.y
        val endY = bubbleWidth + 32// bottom padding

        springCloseAnim = AnimHelper.startSpring(
            startValue = startY.toFloat(),
            finalPosition = endY.toFloat(),
            event = object : AnimHelper.Event {
                override fun onStart() {
                    isCloseViewAnimating = true
                }

                override fun onUpdate(float: Float) {
                    try {
                        closeViewParams.y = float.toInt()
                        windowManager.updateViewLayout(closeView, closeViewParams)

                    } catch (e: Exception) {
//                        Log.e("<>", "onUpdate: ${e.printStackTrace()}")
                    }
                }

                override fun onEnd() {
                    springAnim = null
                    isCloseViewAnimating = false
                    isCloseViewShowing = true
                }
            }
        )
    }

    private fun animateHideCloseView() {
        if (!isBubbleRunning) return

        if (!isCloseViewShowing) return
        springCloseAnim?.cancel()

        val startY = closeViewParams.y
        val endY = -500
        springCloseAnim = AnimHelper.startSpring(
            startValue = startY.toFloat(),
            finalPosition = endY.toFloat(),
            event = object : AnimHelper.Event {
                override fun onStart() {
                    isCloseViewAnimating = true
                }

                override fun onUpdate(float: Float) {
                    try {
                        closeViewParams.y = float.toInt()
                        windowManager.updateViewLayout(closeView, closeViewParams)

                    } catch (e: Exception) {
//                        Log.e("<>", "onUpdate: ${e.printStackTrace()}")
                    }
                }

                override fun onEnd() {
//                    Log.d(TAG, "CloseView: ${closeViewParams.y}")
                    springAnim = null
                    isCloseViewAnimating = false
                    isCloseViewShowing = false
                }
            }
        )
    }

    private fun updatePreviousPosition() {
        previousPosition.apply {
            x = mainView.getPosition().x.toFloat()
            y = mainView.getPosition().y.toFloat()
        }
    }

    private fun isMainViewInCloseViewArea(): Boolean {
        return mainView.getCircleBound().isCollidingWith(closeView.getCircleBound(padding = 20f))
    }

    private var isAnimatingMainViewToCloseView: Boolean = false
    private var mainToCloseSpringAnim: SpringAnimation? = null
    private fun animateMainViewToCloseView(
        onCloseMainView: () -> Unit = {}
    ) {
        mainToCloseSpringAnim?.cancel()
        // Ensure proper calculation of the closeView bounds with padding
        val closeViewBounds = closeView.getCircleBound(padding = 20f)

        val startX = closeView.getPosition().x.toFloat()
        val startY = closeView.getPosition().y.toFloat()

        // Calculate the target coordinates for the animation
        val targetX = closeViewBounds.center.x - mainView.width / 2
        val targetY = closeViewBounds.center.y - mainView.height / 2 + 20f
        mainToCloseSpringAnim = AnimHelper.animateSpringPath(
            startX = startX,
            startY = startY,
            endX = targetX,
            endY = targetY,
            object : AnimHelper.Event {
                override fun onUpdatePoint(x: Float, y: Float) {
                    mainViewParams.x = x.toInt()
                    mainViewParams.y = y.toInt()
                    windowManager.updateViewLayout(mainView, mainViewParams)
                }

                override fun onEnd() {
                    onCloseMainView()
                    mainToCloseSpringAnim = null
                }
            }
        )


    }

    private fun onPositionChanged(newX: Float, newY: Float) {
        mainViewParams.apply {
            this.x = newX.toInt()
            this.y = newY.toInt()
        }
        windowManager.updateViewLayout(mainView, mainViewParams)
        shouldZoomCloseView = isMainViewInCloseViewArea()
    }

    private fun onIsCollapsedChanged(isCollapsed: Boolean) {
        if (!isCollapsed) {
            mainViewParams.apply {
                flags = flags.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    .rem(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                width = WindowManager.LayoutParams.MATCH_PARENT
            }
            onPositionChanged(0f, 0f)

        } else {
            val previousX = mainView.getPosition().x.toFloat()
            val previousY = mainView.getPosition().y.toFloat()
            mainViewParams.apply {
                flags = flags.rem(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    .or(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                width = WindowManager.LayoutParams.WRAP_CONTENT
            }
            onPositionChanged(previousX, previousY)
        }
    }

    private fun onMainViewDragEnd(
        onAnimationEnd: (BubbleAlignment) -> Unit
    ) {
        if (isMainViewInCloseViewArea()) {
            animateMainViewToCloseView {
                closeBubble()
                animateHideCloseView()
            }
        } else {
            animateToEdge(onAnimationEnd = onAnimationEnd)
            animateHideCloseView()
        }

    }

    private var springAnim: SpringAnimation? = null
    private fun animateToEdge(
        onAnimationEnd: (BubbleAlignment) -> Unit
    ) {
        springAnim?.cancel()
        springAnim = null
        val bubbleWidth = mainView.width
        val positionX = mainView.getPosition().x
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val halfScreenWidth = screenWidth / 2
        val isOnTheLeftSide = positionX + bubbleWidth / 2 < halfScreenWidth
        val startX: Int
        val endX: Int
        val alignment: BubbleAlignment
        if (isOnTheLeftSide) {
            startX = positionX
            endX = 0
            alignment = BubbleAlignment.Start
        } else {
            startX = positionX
            endX = screenWidth - bubbleWidth
            alignment = BubbleAlignment.End
        }

        springAnim = AnimHelper.startSpring(
            startValue = startX.toFloat(),
            finalPosition = endX.toFloat(),
            event = object : AnimHelper.Event {
                override fun onUpdate(float: Float) {
                    try {
                        mainViewParams.x = float.toInt()
                        windowManager.updateViewLayout(mainView, mainViewParams)
                    } catch (e: Exception) {
//                        Log.e("<>", "onUpdate: ${e.printStackTrace()}")
                    }
                }

                override fun onEnd() {
                    onAnimationEnd(alignment)
                    springAnim = null
                }
            }
        )
    }

    private fun closeBubble() {
        windowManager.removeView(mainView)
        windowManager.removeView(closeView)
        isBubbleRunning = false
    }


}