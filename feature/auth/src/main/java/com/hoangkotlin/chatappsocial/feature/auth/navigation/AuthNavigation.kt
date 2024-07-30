package com.hoangkotlin.chatappsocial.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.hoangkotlin.chatappsocial.core.common.model.PrefilledData
import com.hoangkotlin.chatappsocial.feature.auth.sign_in.SignInRoute
import com.hoangkotlin.chatappsocial.feature.auth.sign_up.SignUpRoute

const val emailArg = "email"
const val passwordArg = "password"
const val AUTH_GRAPH_ROUTE_PATTERN = "auth_route"
const val signInPattern = "sign_in_route"
const val signInRoute = "sign_in_route?$emailArg={$emailArg}&$passwordArg={$passwordArg}"
const val signUpRoute = "sign_up_route"


fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    this.navigate(AUTH_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavController.navigateToSignUp(navOptions: NavOptions? = null) {
    this.navigate(signUpRoute, navOptions)
}

fun NavController.navigateToSignIn(
    prefilledData: PrefilledData? = null,
    navOptions: NavOptions? = null
) {
    if (prefilledData != null) {
        this.navigate(
            "$signInPattern?$emailArg=${prefilledData.email}" +
                    "&$passwordArg=${prefilledData.password}",
            navOptions
        )
        return
    }
    this.navigate(signInPattern, navOptions)
}

fun NavGraphBuilder.authGraph(
    onNavigateToHome: () -> Unit,
    navController: NavController,
) {
    navigation(
        route = AUTH_GRAPH_ROUTE_PATTERN,
        startDestination = signInRoute
    ) {
        signInRoute(
            onNavigateToHome = onNavigateToHome,
            onNavigateToSignUp = {
                navController.navigateToSignUp(navOptions {
                    launchSingleTop = true
                })
            }
        )
        signUpRoute(
            onNavigateToHome = onNavigateToHome,
            onNavigateToSignIn = { prefilledData ->
                navController.navigateToSignIn(prefilledData, navOptions {
                    this.popUpTo(route = signInRoute) {
                        inclusive = true
                    }
                })
            }
        )
    }
}

fun NavGraphBuilder.signInRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    composable(route = signInRoute,
        arguments = listOf(
            navArgument(emailArg) {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            }, navArgument(passwordArg) {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            }
        )
    ) {
        SignInRoute(
            onNavigateToHome = onNavigateToHome,
            onNavigateToSignUp = onNavigateToSignUp
        )
    }
}

fun NavGraphBuilder.signUpRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToSignIn: (PrefilledData?) -> Unit
) {
    composable(route = signUpRoute) {
        SignUpRoute(
            onNavigateToSignIn = onNavigateToSignIn
        )
    }
}