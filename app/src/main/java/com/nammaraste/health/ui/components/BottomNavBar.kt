package com.nammaraste.health.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nammaraste.health.ui.navigation.Screen

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val pattern: String = route
)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, Screen.Dashboard.route),
        NavItem("Roads", Icons.Filled.Map, Icons.Outlined.Map, Screen.RoadList.route),
        NavItem("Report", Icons.Filled.AddCircle, Icons.Outlined.AddCircle, Screen.ReportDamage.createRoute(), Screen.ReportDamage.route),
        NavItem("Map", Icons.Filled.LocationOn, Icons.Outlined.LocationOn, Screen.MapView.route),
        NavItem("Leaderboard", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, Screen.SuccessMap.route)
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Hide bottom bar on detail and profile screens, and also on report damage if specified by requirement
    // "Hide bottom nav on RoadDetail, ReportDamage, ContractorProfile screens."
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.RoadList.route,
        Screen.MapView.route,
        Screen.SuccessMap.route
    )

    if (showBottomBar) {
        NavigationBar {
            items.forEach { item ->
                val isSelected = currentRoute == item.pattern
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.pattern) {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Dashboard.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    label = { Text(item.label) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    }
                )
            }
        }
    }
}
