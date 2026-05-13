package com.namma_shaale.inventory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.namma_shaale.inventory.presentation.asset.AddAssetScreen
import com.namma_shaale.inventory.presentation.dashboard.DashboardScreen
import com.namma_shaale.inventory.presentation.healthcheck.HealthCheckScreen
import com.namma_shaale.inventory.presentation.issue.IssueRepairScreen
import com.namma_shaale.inventory.presentation.report.ReportScreen
import com.namma_shaale.inventory.presentation.assetlist.AssetListScreen
import com.namma_shaale.inventory.presentation.settings.SettingsScreen
import com.namma_shaale.inventory.presentation.auth.LoginScreen
import com.namma_shaale.inventory.presentation.auth.SignUpScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "login",
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("signup") {
            SignUpScreen(navController = navController)
        }
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
        composable("asset_list") {
            AssetListScreen(navController = navController)
        }
        composable("add_asset") {
            AddAssetScreen(navController = navController)
        }
        composable("health_check") {
            HealthCheckScreen(navController = navController)
        }
        composable("issue_repair") {
            IssueRepairScreen(navController = navController)
        }
        composable("report") {
            ReportScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}
