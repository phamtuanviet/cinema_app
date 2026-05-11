package com.example.myapplication.data.remote.dto

// DTO hiển thị và chi tiết
data class AdminCinemaDto(
    val id: String,
    val name: String,
    val address: String,
    val description: String?,
    val region: String?,
    val cineplex: String?,
    val latitude: Double?,
    val longitude: Double?,
    val logoUrl: String?,
    val isActive: Boolean
)


data class AdminCinemaRequest(
    val name: String,
    val address: String,
    val description: String?,
    val region: String?,
    val cineplex: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean
)

data class AdminCinemaCreateRequest(
    val name: String,
    val address: String,
    val description: String?,
    val region: String?,
    val cineplex: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean,
    val googleMapsLink: String = "",
)



data class AdminCinemaUpdateRequest(
    val name: String,
    val address: String,
    val description: String?,
    val region: String?,
    val cineplex: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean
)

sealed class CinemaCreateEvent {
    data class GoogleMapsLinkChanged(val link: String) : CinemaCreateEvent()
}