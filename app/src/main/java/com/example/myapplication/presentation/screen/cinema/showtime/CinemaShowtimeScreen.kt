package com.example.myapplication.presentation.screen.cinema.showtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.cinema.seat_selection.CinemaSeatSelectionViewModel

@Composable
fun CinemaShowtimeScreen (
    viewModel: CinemaShowtimeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}