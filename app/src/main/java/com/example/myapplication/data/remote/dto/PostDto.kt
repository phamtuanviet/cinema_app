package com.example.myapplication.data.remote.dto

import com.example.myapplication.data.remote.enums.PostType

data class PostResponse(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val type: String,
    val startDate: String?,
    val endDate: String?
)


data class PostDetailResponse(
    val id: String,
    val title: String,
    val content: String,
    val thumbnailUrl: String?,
    val published: Boolean,
    val type: PostType,
    val startDate: String?,
    val endDate: String?,
    val voucher: VoucherPostDto?,
    val isActive: Boolean
)