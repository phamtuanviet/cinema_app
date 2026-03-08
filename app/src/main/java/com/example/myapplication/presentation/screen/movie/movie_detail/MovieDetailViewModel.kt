package com.example.myapplication.presentation.screen.movie.movie_detail

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MovieDetailViewModel(): ViewModel() {

    private val _state = MutableStateFlow(MovieDetailState())
    val state = _state.asStateFlow()
}