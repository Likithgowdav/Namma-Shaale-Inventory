package com.namma_shaale.inventory.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.annotation.StringRes
import com.namma_shaale.inventory.R
import androidx.compose.ui.res.stringResource

sealed class BottomNavItem(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem(R.string.home, Icons.Default.Dashboard, "dashboard")
    object Issues : BottomNavItem(R.string.issues, Icons.Default.Build, "issue_repair")
    object Check : BottomNavItem(R.string.monthly_health_check, Icons.Default.FactCheck, "health_check")
    object Reports : BottomNavItem(R.string.reports, Icons.Default.Assessment, "report")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Issues,
        BottomNavItem.Check,
        BottomNavItem.Reports
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Don't show bottom bar on Add Asset screen or nested screens if desired
    // For now, show it on these 4 main screens
    val showBottomBar = currentRoute in items.map { it.route }

    if (showBottomBar) {
        NavigationBar {
            items.forEach { item ->
                val title = stringResource(item.titleRes)
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = title) },
                    label = { Text(title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
