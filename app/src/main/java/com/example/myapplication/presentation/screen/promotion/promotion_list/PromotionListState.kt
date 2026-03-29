package com.example.myapplication.presentation.screen.promotion.promotion_list

import com.example.myapplication.data.remote.dto.PostResponse

data class PromotionListState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,

    val posts: List<PostResponse> = emptyList(),

    val page: Int = 0,
    val hasMore: Boolean = true,

    val selectedType: String = "VOUCHER",

    val error: String? = null
)