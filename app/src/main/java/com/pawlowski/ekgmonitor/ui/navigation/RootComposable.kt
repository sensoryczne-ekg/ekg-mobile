package com.pawlowski.ekgmonitor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pawlowski.ekgmonitor.ui.screens.chart.ChartScreen
import com.pawlowski.ekgmonitor.ui.screens.chart.ChartViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun RootComposable() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Chart.name) {
        composable(route = Screen.Chart.name) {
            val viewModel = hiltViewModel<ChartViewModel>()
            ChartScreen(
                state = viewModel.stateFlow.collectAsState().value,
                onNewEvent = viewModel::onNewEvent,
            )
            viewModel.navigationFlow.observeNavigation(navController = navController)
        }
    }
}

@Composable
private fun Flow<Direction>.observeNavigation(navController: NavController) {
    LaunchedEffect(Unit) {
        collect { direction ->
            when (direction) {
                is Back -> {
                    navController.popBackStack()
                }

                else -> {
                    navController.navigate(route = direction.destination.name) {
                        launchSingleTop = true
                        direction.popUpTo?.let {
                            popUpTo(route = it.name) {
                                inclusive = direction.popUpToInclusive
                            }
                        }
                    }
                }
            }
        }
    }
}
