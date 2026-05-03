package com.example.myapplication.presentation.screen.profile.ticket_detail

import com.example.myapplication.data.remote.dto.BookingMyBookingDto

data class ProfileTicketDetailState(
    val isLoading: Boolean = true,
    val booking: BookingMyBookingDto? = null, // Ban đầu chưa có vé thì null
    val error: String? = null
)