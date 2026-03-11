package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.MovieRoute
import com.example.myapplication.presentation.screen.movie.booking.MovieBookingScreen
import com.example.myapplication.presentation.screen.movie.checkout.MovieCheckoutScreen
import com.example.myapplication.presentation.screen.movie.movie_detail.MovieDetailScreen
import com.example.myapplication.presentation.screen.movie.movie_list.MovieListScreen
import com.example.myapplication.presentation.screen.movie.seat_selection.MovieSeatSelectionScreen
import com.example.myapplication.presentation.screen.movie.showtime.MovieShowtimeScreen

fun NavGraphBuilder.movieNavGraph(
    navController: NavHostController
) {

    navigation(
        route = MainRoute.MovieGraph.route,
        startDestination = MovieRoute.MovieList.route
    ) {

        composable(MovieRoute.MovieList.route) {
            MovieListScreen(
                onNavigateBooking = { movieId ->
                    navController.navigate(MovieRoute.MovieBooking.createRoute(movieId))
                }
            )
        }

        composable(
            route = MovieRoute.MovieBooking.route,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""

            MovieBookingScreen(
                movieId = movieId,
                onMovieDetailClick = { movieId ->
                    navController.navigate(MovieRoute.MovieDetail.createRoute(movieId))
                },
                onShowtimeClick = { showtimeId ->
                    navController.navigate(
                        MovieRoute.SeatSelection.createRoute(showtimeId)
                    )
                }
            )
        }

        composable(
            MovieRoute.MovieDetail.route,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            MovieDetailScreen(
                movieId = movieId
            )
        }

        composable(MovieRoute.Showtime.route) {
            MovieShowtimeScreen()
        }

        composable(
            route = MovieRoute.SeatSelection.route,
            arguments = listOf(
                navArgument("showtimeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val showtimeId =
                backStackEntry.arguments?.getString("showtimeId")!!

            MovieSeatSelectionScreen(
                showtimeId = showtimeId,
            )
        }

        composable(MovieRoute.Checkout.route) {
            MovieCheckoutScreen()
        }

    }
}