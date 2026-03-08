package com.example.myapplication.presentation.screen.cinema.cinema_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CinemaListScreen (
    viewModel: CinemaListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}