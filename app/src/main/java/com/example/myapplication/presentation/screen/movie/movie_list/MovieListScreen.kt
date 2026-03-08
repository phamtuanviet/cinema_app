package com.example.myapplication.presentation.screen.movie.movie_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.movie.movie_detail.MovieDetailViewModel

@Composable
fun MovieListScreen (
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}