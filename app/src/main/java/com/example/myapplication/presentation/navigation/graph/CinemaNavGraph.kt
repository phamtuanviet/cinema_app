package com.example.myapplication.presentation.navigation.graph

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.BookingRoute
import com.example.myapplication.presentation.navigation.route.CinemaRoute
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutScreen
import com.example.myapplication.presentation.screen.cinema.cinema_detail.CinemaDetailScreen
import com.example.myapplication.presentation.screen.cinema.cinema_list.CinemaListScreen
import com.example.myapplication.presentation.screen.cinema.seat_selection.CinemaSeatSelectionScreen
import com.example.myapplication.presentation.screen.cinema.showtime.CinemaShowtimeScreen
import kotlin.collections.listOf

fun NavGraphBuilder.cinemaNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController
) {

    navigation(
        route = MainRoute.CinemaGraph.route,
        startDestination = CinemaRoute.CinemaList.route
    ) {

        composable(CinemaRoute.CinemaList.route) {
            CinemaListScreen(
                onCinemaClick = { cinemaId ->
                    val route = CinemaRoute.CinemaDetail.createRoute(cinemaId)
                    navController.navigate(route)
                }
            )
        }

        composable(
            route = CinemaRoute.CinemaDetail.route,
            arguments = listOf(
                navArgument("cinemaId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val cinemaId =
                backStackEntry.arguments?.getString("cinemaId").orEmpty()

            CinemaDetailScreen(
                cinemaId = cinemaId,

                onShowtimeClick = { showtimeId, movieId ->
                    navController.navigate(
                        BookingRoute.SeatSelection.createRoute(
                            showtimeId,
                            movieId
                        )
                    )
                }
            )
        }
    }
}