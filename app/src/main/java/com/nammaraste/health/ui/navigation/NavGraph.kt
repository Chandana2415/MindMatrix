package com.nammaraste.health.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.nammaraste.health.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onRoadClick = { roadId -> navController.navigate(Screen.RoadDetail.createRoute(roadId)) },
                onReportClick = { navController.navigate(Screen.ReportDamage.createRoute()) },
                onSeeAllRoads = { navController.navigate(Screen.RoadList.route) }
            )
        }
        composable(Screen.RoadList.route) {
            RoadListScreen(
                onRoadClick = { roadId -> navController.navigate(Screen.RoadDetail.createRoute(roadId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.RoadDetail.route,
            arguments = listOf(navArgument("roadId") { type = NavType.IntType })
        ) { backStackEntry ->
            val roadId = backStackEntry.arguments?.getInt("roadId") ?: -1
            RoadDetailScreen(
                roadId = roadId,
                onBack = { navController.popBackStack() },
                onReportClick = { id -> navController.navigate(Screen.ReportDamage.createRoute(id)) },
                onContractorClick = { name -> navController.navigate(Screen.ContractorProfile.createRoute(name)) }
            )
        }
        composable(
            route = Screen.ReportDamage.route,
            arguments = listOf(navArgument("roadId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val roadId = backStackEntry.arguments?.getInt("roadId") ?: -1
            ReportDamageScreen(
                roadId = roadId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.MapView.route) {
            MapScreen(
                onRoadClick = { roadId -> navController.navigate(Screen.RoadDetail.createRoute(roadId)) },
                onReportDamageClick = { roadId -> navController.navigate(Screen.ReportDamage.createRoute(roadId)) }
            )
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                onRoadClick = { roadId -> navController.navigate(Screen.RoadDetail.createRoute(roadId)) }
            )
        }
        composable(
            route = Screen.ContractorProfile.route,
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            ContractorProfileScreen(
                contractorName = name,
                onBack = { navController.popBackStack() },
                onRoadClick = { roadId -> navController.navigate(Screen.RoadDetail.createRoute(roadId)) }
            )
        }
    }
}
