package com.example.myapplication.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val message: String,
    val chatSessionId: String,
    val location: UserLocation? = null
)

data class UserLocation(
    val lat: Double,
    val lng: Double
)

// --- RESPONSE NHẬN VỀ ---
data class ChatResponse(
    val status: String,
    val content: String,
    // Dùng @SerializedName để khớp với key "ui_actions" từ Node.js
    @SerializedName("ui_actions")
    val uiActions: List<RawUiAction>? = null
)

// Lớp này dùng để hứng cục JSON thô từ phần tử data
data class RawUiAction(
    val type: String,
    val data: JsonElement?
)

// Lớp bổ trợ cho các API trả về kiểu Page<T> từ Spring Boot
data class SpringPageResponse<T>(
    val content: List<T>?,
    val totalPages: Int?,
    val totalElements: Long?,
    val number: Int?,
    val last: Boolean?,
    val size: Int?
)