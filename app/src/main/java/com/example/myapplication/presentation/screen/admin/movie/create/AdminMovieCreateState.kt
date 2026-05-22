package com.example.myapplication.presentation.screen.admin.movie.create

import android.net.Uri
import com.example.myapplication.data.remote.dto.AdminGenreDto

data class AdminMovieCreateState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Dùng cho Toast chung (như quên chọn ảnh, lỗi mạng)

    val title: String = "",
    val titleError: String? = null, // Lỗi riêng cho ô Tên phim

    val description: String = "",
    val durationMinutes: String = "",
    val durationError: String? = null, // Lỗi riêng ô Thời lượng

    val releaseDate: String = "",
    val releaseDateError: String? = null, // Lỗi ô Ngày chiếu

    val basePrice: String = "",
    val priceError: String? = null, // Lỗi ô Giá vé

    val trailerUrl: String = "",
    val language: String = "Tiếng Việt",
    val ageRating: String = "P",
    val isActive: Boolean = true,
    val sendNotification: Boolean = false,

    val posterUri: Uri? = null,

    val availableGenres: List<AdminGenreDto> = emptyList(),
    val selectedGenres: List<AdminGenreDto> = emptyList(),
    val newGenres: List<String> = emptyList()
)