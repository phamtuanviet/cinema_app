package com.example.myapplication.presentation.screen.profile.ticket_detail

import com.example.myapplication.data.remote.dto.BookingMyBookingDto

data class ProfileTicketDetailState(
    val isLoading: Boolean = true,
    val isRefunding: Boolean = false, // Trạng thái đang gọi API hoàn tiền
    val refundSuccess: Boolean = false, // Trạng thái hoàn tiền thành công
    val booking: BookingMyBookingDto? = null,
    val error: String? = null
)