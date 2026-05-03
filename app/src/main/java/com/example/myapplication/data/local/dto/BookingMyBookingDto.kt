package com.example.myapplication.data.local.dto

import com.example.myapplication.data.local.entity.BookingEntity
import com.example.myapplication.data.remote.dto.BookingComboMyBookingDto
import com.example.myapplication.data.remote.dto.BookingMyBookingDto
import com.example.myapplication.data.remote.dto.CinemaMyBookingDto
import com.example.myapplication.data.remote.dto.MovieMyBookingDto
import com.example.myapplication.data.remote.dto.RoomMyBookingDto
import com.example.myapplication.data.remote.dto.SeatMyBookingDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

// Từ API (DTO) chuyển sang Room (Entity) để lưu trữ
fun BookingMyBookingDto.toEntity(): BookingEntity {
    return BookingEntity(
        id = id,
        ticketCode = ticketCode,
        qrCodeUrl = qrCodeUrl,
        status = status,
        totalAmount = totalAmount,
        showtimeStart = showtimeStart,
        showtimeEnd = showtimeEnd,
        movieJson = gson.toJson(movie),
        cinemaJson = gson.toJson(cinema),
        roomJson = gson.toJson(room),
        seatsJson = gson.toJson(seats),
        combosJson = gson.toJson(combos),
        userRating = userRating,
        averageRating = averageRating
    )
}

// Từ Room (Entity) chuyển ngược lại cho UI (DTO) hiển thị
fun BookingEntity.toDto(): BookingMyBookingDto {
    val seatListType = object : TypeToken<List<SeatMyBookingDto>>() {}.type
    val comboListType = object : TypeToken<List<BookingComboMyBookingDto>>() {}.type

    return BookingMyBookingDto(
        id = id,
        ticketCode = ticketCode,
        qrCodeUrl = qrCodeUrl,
        status = status,
        totalAmount = totalAmount,
        showtimeStart = showtimeStart,
        showtimeEnd = showtimeEnd,
        movie = gson.fromJson(movieJson, MovieMyBookingDto::class.java),
        cinema = gson.fromJson(cinemaJson, CinemaMyBookingDto::class.java),
        room = gson.fromJson(roomJson, RoomMyBookingDto::class.java),
        seats = gson.fromJson(seatsJson, seatListType),
        combos = gson.fromJson(combosJson, comboListType),
        userRating = userRating,
        averageRating = averageRating,
        seatAmount = 0.0, comboAmount = 0.0, voucherDiscount = 0.0, pointDiscount = 0.0
    )
}