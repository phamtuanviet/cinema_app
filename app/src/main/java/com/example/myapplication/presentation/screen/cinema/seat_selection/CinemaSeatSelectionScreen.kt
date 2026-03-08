package com.example.myapplication.presentation.screen.cinema.seat_selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.cinema.cinema_detail.CinemaDetailViewModel

@Composable
fun CinemaSeatSelectionScreen (
    viewModel: CinemaSeatSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}