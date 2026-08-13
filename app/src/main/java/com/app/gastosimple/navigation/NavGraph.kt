package com.app.gastosimple.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.gastosimple.R
import com.app.gastosimple.core.data.prefs.UserPreferencesRepository
import com.app.gastosimple.features.calendar.CalendarScreen
import com.app.gastosimple.features.expenses.ExpenseListScreen
import com.app.gastosimple.features.setup.SetupScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

sealed class Screen(val route: String, val icon: @Composable () -> Unit, val labelId: Int) {
    object Expenses : Screen("expenses", { Icon(Icons.Default.Home, null) }, R.string.nav_expenses)
    object Calendar : Screen("calendar", { Icon(Icons.Default.DateRange, null) }, R.string.nav_calendar)
    object Dashboard : Screen("dashboard", { Icon(Icons.Default.ShoppingCart, null) }, R.string.nav_dashboard)
    object Settings : Screen("settings", { Icon(Icons.Default.Settings, null) }, R.string.nav_settings)
    object Setup : Screen("setup", {}, 0)
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val prefs: UserPreferencesRepository = koinInject()
    val isSetupComplete by prefs.isSetupComplete.collectAsState(initial = null)

    if (isSetupComplete == null) return // Loading

    val startDestination = if (isSetupComplete!!) Screen.Expenses.route else Screen.Setup.route

    Scaffold(
        bottomBar = {
            if (isSetupComplete == true) {
                BottomBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Setup.route) {
                SetupScreen(viewModel = koinViewModel(), onFinished = {
                    navController.navigate(Screen.Expenses.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Expenses.route) {
                ExpenseListScreen(viewModel = koinViewModel())
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(viewModel = koinViewModel())
            }
            composable(Screen.Dashboard.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.nav_dashboard))
                }
            }
            composable(Screen.Settings.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.nav_settings))
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: androidx.navigation.NavHostController) {
    val items = listOf(Screen.Expenses, Screen.Calendar, Screen.Dashboard, Screen.Settings)
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = screen.icon,
                label = { Text(stringResource(screen.labelId)) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
