package com.example.myapplication.presentation.screen.movie.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.cinema.showtime.CinemaShowtimeViewModel

@Composable
fun MovieCheckoutScreen (
    viewModel: MovieCheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}