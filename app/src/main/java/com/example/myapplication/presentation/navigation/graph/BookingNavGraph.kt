package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.myapplication.presentation.navigation.route.BookingRoute
import com.example.myapplication.presentation.screen.booking.checkout.BookingCheckoutScreen
import com.example.myapplication.presentation.screen.booking.other_options.BookingOtherOptionsScreen
import com.example.myapplication.presentation.screen.booking.seat_selection.BookingSeatSelectionScreen
import com.example.myapplication.presentation.screen.booking.failed.PaymentFailedScreen
import com.example.myapplication.presentation.screen.booking.sucess.PaymentSuccessScreen


fun NavGraphBuilder.bookingNavGraph(
    navController: NavHostController,
    navRootController: NavHostController
) {

    navigation(
        route = "booking_graph",
        startDestination = BookingRoute.SeatSelection.route
    ) {

        composable(
            route = BookingRoute.SeatSelection.route,
            arguments = listOf(
                navArgument("showtimeId") {
                    type = NavType.StringType
                },
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val showtimeId =
                backStackEntry.arguments?.getString("showtimeId").orEmpty()

            val movieId =
                backStackEntry.arguments?.getString("movieId").orEmpty()

            BookingSeatSelectionScreen(
                showtimeId = showtimeId,

                onContinueClick = { sessionId ->
                    navController.navigate(
                        BookingRoute.OtherOptions.createRoute(sessionId)
                    )
                },

                onSessionExpired = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = BookingRoute.OtherOptions.route,
            arguments = listOf(
                navArgument("seatHoldSessionId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val sessionId =
                backStackEntry.arguments?.getString("seatHoldSessionId").orEmpty()

            BookingOtherOptionsScreen(
                seatHoldSessionId = sessionId,

                onSuccessResult = { bookingId ->
                    navController.navigate(
                        BookingRoute.Checkout.createRoute(bookingId)
                    )
                }
            )
        }

        composable(
            route = BookingRoute.Checkout.route,
            arguments = listOf(
                navArgument("bookingId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val bookingId =
                backStackEntry.arguments?.getString("bookingId").orEmpty()

            BookingCheckoutScreen(
                bookingId = bookingId,

                onPaymentSuccess = {
                    navController.navigate(BookingRoute.PaymentSuccess.route) {
                        popUpTo("booking_graph") { inclusive = true }
                    }
                },

                onPaymentFailed = {
                    navController.navigate(BookingRoute.PaymentFailed.route)
                }
            )
        }

        composable(BookingRoute.PaymentSuccess.route) {
            PaymentSuccessScreen(
                onBackHome = {
                    navController.popBackStack(
                        route = "booking_graph",
                        inclusive = true
                    )
                }
            )
        }

        composable(BookingRoute.PaymentFailed.route) {
            PaymentFailedScreen(
                onRetry = {
                    navController.popBackStack()
                }
            )
        }
    }
}