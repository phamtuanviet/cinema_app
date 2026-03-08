package com.example.myapplication.presentation.screen.promotion.promotion_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.screen.profile.settings.ProfileSettingsViewModel

@Composable
fun PromotionDetailScreen (
    viewModel: PromotionDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

}