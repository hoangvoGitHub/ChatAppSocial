package com.hoangkotlin.chatappsocial.ui.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hoangkotlin.chatappsocial.feature.auth.navigation.AUTH_GRAPH_ROUTE_PATTERN
import com.hoangkotlin.chatappsocial.feature.auth.navigation.authGraph

@Composable
fun AuthNavHost(
    navController: NavHostController,
    startDestination: String = AUTH_GRAPH_ROUTE_PATTERN,
    onNavigateToHome: () -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {
        authGraph(
            navController = navController,
            onNavigateToHome = onNavigateToHome
        )
    }
}