package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.BookingRoute
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.MovieRoute
import com.example.myapplication.presentation.screen.movie.booking.MovieBookingScreen
import com.example.myapplication.presentation.screen.movie.movie_detail.MovieDetailScreen
import com.example.myapplication.presentation.screen.movie.movie_list.MovieListScreen
import com.example.myapplication.presentation.screen.booking.failed.PaymentFailedScreen
import com.example.myapplication.presentation.screen.booking.sucess.PaymentSuccessScreen

fun NavGraphBuilder.movieNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController
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


        composable("payment_success") {
            PaymentSuccessScreen(
                onBackHome = {
                    navController.navigate(MovieRoute.MovieList.route) {
                        popUpTo(MainRoute.MovieGraph.route)
                    }
                }
            )
        }

        composable("payment_failed") {
            PaymentFailedScreen(
                onRetry = {
                    navController.popBackStack()
                }
            )
        }

    }
}