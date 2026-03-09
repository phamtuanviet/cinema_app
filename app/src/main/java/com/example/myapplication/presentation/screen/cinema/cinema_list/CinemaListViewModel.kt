package com.example.myapplication.presentation.screen.cinema.cinema_list

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CinemaListViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(CinemaListState())
    val state = _state.asStateFlow()
}