package com.hoangkotlin.chatappsocial.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalView
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.hoangkotlin.chatappsocial.feature.auth.navigation.AUTH_GRAPH_ROUTE_PATTERN
import com.hoangkotlin.chatappsocial.feature.auth.navigation.navigateToAuthGraph
import com.hoangkotlin.chatappsocial.feature.home.navigation.channelsRoute
import com.hoangkotlin.chatappsocial.feature.home.navigation.navigateToHomeGraph
import com.hoangkotlin.chatappsocial.feature.profile.navigation.navigateToProfileGraph
import com.hoangkotlin.chatappsocial.feature.profile.navigation.profileRoute
import com.hoangkotlin.chatappsocial.ui.main.navigation.TopLevelDestination
import com.hoangkotlin.chatappsocial.ui.main.navigation.TopLevelDestination.FRIENDS
import com.hoangkotlin.chatappsocial.ui.main.navigation.TopLevelDestination.HOME
import com.hoangkotlin.chatappsocial.ui.main.navigation.TopLevelDestination.PROFILE
import com.hoangkotlin.chatappsocial.feature.friend.navigation.friendsRoute
import com.hoangkotlin.chatappsocial.feature.friend.navigation.navigateToFriendsGraph
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberSocialAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): SocialAppState {
    NavigationTrackingSideEffect(navController)
    return remember(
        coroutineScope,
        navController
    ) {
        SocialAppState(
            navController, coroutineScope
        )
    }

}

private const val TAG = "SocialAppState"

class SocialAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val shouldShowDrawer: Boolean
        @Composable
        get() = currentDestination?.route?.shouldShowDrawer() ?: false

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            channelsRoute -> HOME
            profileRoute -> PROFILE
            friendsRoute -> FRIENDS
            else -> null
        }


    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
//        trace("Navigation: ${topLevelDestination.name}") {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            HOME -> navController.navigateToHomeGraph(topLevelNavOptions)
            PROFILE -> navController.navigateToProfileGraph(topLevelNavOptions)
            FRIENDS -> navController.navigateToFriendsGraph(topLevelNavOptions)
        }
    }

    fun logOut() {
        navController.navigateToAuthGraph(
            navOptions {
                popUpTo(AUTH_GRAPH_ROUTE_PATTERN)
            }
        )
    }
}

@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    TrackDisposableJank(navController) { metricsHolder ->
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            metricsHolder.state?.putState("Navigation", destination.route.toString())
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}

@Composable
fun TrackDisposableJank(
    vararg keys: Any?,
    reportMetric: DisposableEffectScope.(state: PerformanceMetricsState.Holder) -> DisposableEffectResult,
) {
    val metrics = rememberMetricsStateHolder()
    DisposableEffect(metrics, *keys) {
        reportMetric(this, metrics)
    }
}

@Composable
fun rememberMetricsStateHolder(): PerformanceMetricsState.Holder {
    val localView = LocalView.current

    return remember(localView) {
        PerformanceMetricsState.getHolderForHierarchy(localView)
    }
}

fun String.shouldShowDrawer(): Boolean {
    return this == channelsRoute ||
            this == friendsRoute ||
            this == profileRoute
}
