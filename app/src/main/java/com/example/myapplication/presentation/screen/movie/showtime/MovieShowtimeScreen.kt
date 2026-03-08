package com.example.myapplication.presentation.screen.movie.showtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.movie.seat_selection.MovieSeatSelectionViewModel

@Composable
fun MovieShowtimeScreen (
    viewModel: MovieShowtimeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}