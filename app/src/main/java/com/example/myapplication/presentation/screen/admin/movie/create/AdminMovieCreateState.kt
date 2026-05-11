package com.example.myapplication.presentation.screen.admin.movie.create

import android.net.Uri
import com.example.myapplication.data.remote.dto.AdminGenreDto


data class AdminMovieCreateState(
    val title: String = "",
    val description: String = "",
    val durationMinutes: String = "", // Để String để dễ nhập từ TextField
    val releaseDate: String = "",
    val basePrice: String = "",
    val trailerUrl: String = "",
    val language: String = "Tiếng Việt",
    val ageRating: String = "P",
    val isActive: Boolean = true,

    val posterUri: Uri? = null,

    val availableGenres: List<AdminGenreDto> = emptyList(),
    val selectedGenres: List<AdminGenreDto> = emptyList(), // Các thể loại đã có
    val newGenres: List<String> = emptyList(),             // Các thể loại mới người dùng gõ thêm

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)