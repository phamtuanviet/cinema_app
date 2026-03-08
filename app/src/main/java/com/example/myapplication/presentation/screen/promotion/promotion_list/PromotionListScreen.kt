package com.example.myapplication.presentation.screen.promotion.promotion_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.promotion.promotion_detail.PromotionDetailViewModel

@Composable
fun PromotionListScreen (
    viewModel: PromotionListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}