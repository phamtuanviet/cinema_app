package com.example.myapplication.presentation.screen.movie.seat_selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.movie.movie_list.MovieListViewModel

@Composable
fun MovieSeatSelectionScreen (
    viewModel: MovieSeatSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}