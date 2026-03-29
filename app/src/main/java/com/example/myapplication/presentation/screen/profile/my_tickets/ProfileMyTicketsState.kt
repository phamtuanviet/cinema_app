package com.example.myapplication.presentation.screen.profile.my_tickets

import com.example.myapplication.data.remote.dto.BookingMyBookingDto
import com.example.myapplication.data.remote.enums.BookingTab

data class ProfileMyTicketsState(
    val selectedTab: BookingTab = BookingTab.UPCOMING,

    val upcoming: List<BookingMyBookingDto> = emptyList(),
    val ongoing: List<BookingMyBookingDto> = emptyList(),
    val completed: List<BookingMyBookingDto> = emptyList(),

    val isLoadingUpcoming: Boolean = false,
    val isLoadingOngoing: Boolean = false,
    val isLoadingCompleted: Boolean = false,

    val ratingLoadingIds: Set<String> = emptySet()
)