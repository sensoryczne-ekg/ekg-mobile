package com.pawlowski.ekgmonitor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pawlowski.ekgmonitor.ui.screens.chart.ChartScreen
import com.pawlowski.ekgmonitor.ui.screens.chart.ChartViewModel
import com.pawlowski.ekgmonitor.ui.screens.choosePeriod.ChoosePeriodScreen
import com.pawlowski.ekgmonitor.ui.screens.history.HistoryDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
internal fun RootComposable() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Chart.name) {
        composable(route = Screen.Chart.name) {
            val viewModel = hiltViewModel<ChartViewModel>()
            viewModel.observeRecordsFlow.collectAsStateWithLifecycle()
            ChartScreen(
                state = viewModel.stateFlow.collectAsState().value,
                onNewEvent = viewModel::onNewEvent,
            )
            viewModel.navigationFlow.observeNavigation(navController = navController)
        }
        composable(route = Screen.ChoosePeriod.name) {
            ChoosePeriodScreen(
                onConfirmClick = { from, to ->
                    navController.navigate(
                        route =
                            Screen.History(
                                from = from.toInstant(timeZone = TimeZone.currentSystemDefault()).epochSeconds,
                                to = to.toInstant(timeZone = TimeZone.currentSystemDefault()).epochSeconds,
                            ).nameForNavigation,
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
        composable(
            route = Screen.History.NAME,
            arguments =
                listOf(
                    navArgument("from") {
                        type = NavType.LongType
                    },
                    navArgument("to") {
                        type = NavType.LongType
                    },
                ),
        ) {
            HistoryDestination()
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
                    navController.navigate(route = direction.destination.nameForNavigation) {
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
