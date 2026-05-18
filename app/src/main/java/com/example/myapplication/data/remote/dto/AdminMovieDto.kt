package com.example.myapplication.data.remote.dto

// 1. Data model nhận dữ liệu phân trang từ Spring Boot
// 1. DTO Thể loại
data class AdminGenreDto(
    val id: String,
    val name: String
)

// 2. DTO Phim đầy đủ trường như Entity
data class AdminMovieDto(
    val id: String,
    val title: String,
    val description: String?,
    val durationMinutes: Int?,
    val releaseDate: String?, // Format: "YYYY-MM-DD"
    val basePrice: Double?,   // Hoặc BigDecimal tùy vào setup của bạn
    val posterUrl: String?,
    val trailerUrl: String?,
    val ageRating: String?,   // Ví dụ: "C18", "P"
    val language: String?,
    val isActive: Boolean,
    val genres: List<AdminGenreDto> = emptyList()
)

// 3. Paginated Response
data class AdminPaginatedResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val totalPages: Int,
    val totalElements: Long,
    val isLast: Boolean
)


data class AdminMovieCreateRequest(
    val title: String,
    val description: String?,
    val durationMinutes: Int?,
    val releaseDate: String?,
    val basePrice: Double?,
    val ageRating: String?,
    val language: String?,
    val trailerUrl: String?,
    val isActive: Boolean,
    val genreIds: List<String>,
    val newGenres: List<String>,
    val sendNotification: Boolean
)

data class AdminMovieUpdateRequest(
    val title: String,
    val description: String?,
    val durationMinutes: Int?,
    val releaseDate: String?,
    val basePrice: Double?,
    val ageRating: String?,
    val language: String?,
    val trailerUrl: String?,
    val isActive: Boolean,
    val genreIds: List<String>,
    val newGenres: List<String>
)