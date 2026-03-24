package com.example.myapplication.presentation.screen.cinema.cinema_list

import com.example.myapplication.data.remote.dto.CinemaResponse
import com.example.myapplication.data.remote.dto.RegionResponse

data class CinemaListState(
    val isLoading: Boolean = false,
    val lat: Double? = null,
    val lng: Double? = null,
    val nearbyCinemas: List<CinemaResponse> = emptyList(),

    val regions: List<RegionResponse> = emptyList(),

    // lưu cinema theo từng region (cache)
    val cinemasByRegion: Map<String, List<CinemaResponse>> = emptyMap(),

    // region nào đang mở
    val expandedRegion: String? = null,

    val error: String? = null
)