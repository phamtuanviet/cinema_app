package com.example.myapplication.presentation.screen.profile.my_tickets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.movie.showtime.MovieShowtimeViewModel

@Composable
fun ProfileMyTicketsScreen (
    viewModel: ProfileMyTicketsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}