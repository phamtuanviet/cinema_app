package com.example.myapplication.presentation.screen.promotion.promotion_detail

import com.example.myapplication.data.remote.dto.PostDetailResponse

data class PromotionDetailState(
    val isLoading: Boolean = false,
    val post: PostDetailResponse? = null,
    val error: String? = null
)