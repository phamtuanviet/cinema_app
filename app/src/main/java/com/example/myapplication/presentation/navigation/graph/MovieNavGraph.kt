package com.example.myapplication.presentation.navigation.graph

import android.util.Log
import androidx.compose.ui.platform.LocalContext
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
import com.example.myapplication.presentation.screen.movie.other_options.MovieOtherOptionsScreen
import com.example.myapplication.presentation.screen.movie.seat_selection.MovieSeatSelectionScreen
import com.example.myapplication.presentation.screen.movie.showtime.MovieShowtimeScreen
import com.example.myapplication.presentation.screen.payment.failed.PaymentFailedScreen
import com.example.myapplication.presentation.screen.payment.sucess.PaymentSuccessScreen
import com.example.myapplication.utils.openPayment
import java.util.UUID

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
                        MovieRoute.SeatSelection.createRoute(showtimeId, movieId)
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
                navArgument("movieId") {
                    type = NavType.StringType
                },
                navArgument("showtimeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val movieId =
                backStackEntry.arguments?.getString("movieId")!!

            val showtimeId =
                backStackEntry.arguments?.getString("showtimeId")!!

            MovieSeatSelectionScreen(
                showtimeId = showtimeId,
                onContinueClick = { sessionId ->

                    navController.navigate(
                        MovieRoute.OtherOptions.createRoute(
                            seatHoldSessionId = sessionId
                        )
                    )
                },
                onSessionExpired = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = MovieRoute.OtherOptions.route,
            arguments = listOf(
                navArgument("seatHoldSessionId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val sessionId =
                backStackEntry.arguments?.getString("seatHoldSessionId")!!

            MovieOtherOptionsScreen(
                seatHoldSessionId = sessionId,
                onSuccessResult = { bookingId ->
                    Log.d("MovieOtherOptionsScreen", "onSuccessResult: $bookingId")
                    navController.navigate(
                        MovieRoute.Checkout.createRoute(bookingId)
                    )
                }
            )
        }

        composable(
            route = MovieRoute.Checkout.route,
            arguments = listOf(
                navArgument("bookingId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val bookingId = backStackEntry.arguments
                ?.getString("bookingId")
                .orEmpty()

            MovieCheckoutScreen(
                bookingId = bookingId,

                onPaymentSuccess = {
                    navController.navigate("payment_success") {
                        popUpTo(MovieRoute.MovieList.route)
                    }
                },
                onPaymentFailed = {
                    navController.navigate("payment_failed")
                }
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