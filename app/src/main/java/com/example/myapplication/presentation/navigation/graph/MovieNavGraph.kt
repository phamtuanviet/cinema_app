package com.example.myapplication.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.presentation.navigation.route.MainRoute
import com.example.myapplication.presentation.navigation.route.MovieRoute
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
            MovieListScreen()
        }

        composable(MovieRoute.MovieDetail.route) {
            MovieDetailScreen()
        }

        composable(MovieRoute.Showtime.route) {
            MovieShowtimeScreen()
        }

        composable(MovieRoute.SeatSelection.route) {
            MovieSeatSelectionScreen()
        }

        composable(MovieRoute.Checkout.route) {
            MovieCheckoutScreen()
        }

    }
}