package com.example.myapplication.presentation.screen.promotion.promotion_list

import androidx.lifecycle.ViewModel
import com.example.myapplication.presentation.screen.cinema.checkout.CinemaCheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class PromotionListViewModel(): ViewModel() {

    private val _state = MutableStateFlow(PromotionListState())
    val state = _state.asStateFlow()
}