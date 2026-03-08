package com.example.myapplication.presentation.screen.movie.checkout

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MovieCheckoutViewModel(): ViewModel() {

    private val _state = MutableStateFlow(MovieCheckoutState())
    val state = _state.asStateFlow()
}