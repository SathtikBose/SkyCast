package com.buildstack.skycast

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buildstack.skycast.presentation.airquality.AqiScreen
import com.buildstack.skycast.presentation.home.HomeScreen
import com.buildstack.skycast.presentation.search.SearchScreen
import com.buildstack.skycast.presentation.settings.SettingsScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, 
        startDestination = "home",
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) }
    ) {
        composable("home") { backStackEntry ->
            val lat = backStackEntry.savedStateHandle.get<Double>("selected_location_lat")
            val lon = backStackEntry.savedStateHandle.get<Double>("selected_location_lon")

            HomeScreen(
                selectedLat = lat,
                selectedLon = lon,
                onLocationConsumed = {
                    backStackEntry.savedStateHandle.remove<Double>("selected_location_lat")
                    backStackEntry.savedStateHandle.remove<Double>("selected_location_lon")
                },
                onSearchClick = { navController.navigate("search") },
                onAqiClick = { currentLat, currentLon ->
                    if (currentLat != null && currentLon != null) {
                        navController.navigate("aqi/$currentLat/$currentLon")
                    }
                },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("search") {
            SearchScreen(
                onLocationSelected = { location ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_location_lat", location.lat)
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_location_lon", location.lon)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "aqi/{lat}/{lon}",
            arguments = listOf(
                androidx.navigation.navArgument("lat") { type = androidx.navigation.NavType.FloatType },
                androidx.navigation.navArgument("lon") { type = androidx.navigation.NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 40.7128
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: -74.0060
            AqiScreen(
                lat = lat,
                lon = lon,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
