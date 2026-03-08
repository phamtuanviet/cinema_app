package com.example.myapplication.presentation.screen.movie.movie_list

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MovieListViewModel(): ViewModel() {

    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()
}