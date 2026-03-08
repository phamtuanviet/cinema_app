package com.example.myapplication.presentation.screen.cinema.seat_selection

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CinemaSeatSelectionViewModel(): ViewModel() {

    private val _state = MutableStateFlow(CinemaSeatSelectionState())
    val state = _state.asStateFlow()
}