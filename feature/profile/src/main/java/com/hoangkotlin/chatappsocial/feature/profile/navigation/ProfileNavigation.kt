package com.hoangkotlin.chatappsocial.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hoangkotlin.chatappsocial.feature.profile.ProfileRoute

const val profileRoute = "profile_route"
private const val PROFILE_GRAPH_ROUTE_PATTERN = "profile_graph"

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null){
    this.navigate(PROFILE_GRAPH_ROUTE_PATTERN,navOptions)
}
fun NavGraphBuilder.profileGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit,
){
    navigation(
        route = PROFILE_GRAPH_ROUTE_PATTERN,
        startDestination = profileRoute
    ){
        composable(route = profileRoute){
            ProfileRoute()
        }
        nestedGraphs()
    }
}