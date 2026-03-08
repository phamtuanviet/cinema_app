package com.example.myapplication.presentation.screen.promotion.promotion_detail

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class PromotionDetailViewModel(): ViewModel() {

    private val _state = MutableStateFlow(PromotionDetailState())
    val state = _state.asStateFlow()
}