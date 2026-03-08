package com.example.myapplication.presentation.screen.cinema.showtime

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CinemaShowtimeViewModel(): ViewModel() {

    private val _state = MutableStateFlow((CinemaShowtimeState()))
    val state = _state.asStateFlow()
}