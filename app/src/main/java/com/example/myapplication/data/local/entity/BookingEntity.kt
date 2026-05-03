package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val ticketCode: String,
    val qrCodeUrl: String?,
    val status: String, // UPCOMING, ONGOING, COMPLETED

    val totalAmount: Double,
    val showtimeStart: String,
    val showtimeEnd: String,

    // Các object phức tạp sẽ được chuyển thành String (JSON) bằng TypeConverter
    val movieJson: String,
    val cinemaJson: String,
    val roomJson: String,
    val seatsJson: String,
    val combosJson: String,

    val userRating: Int?,
    val averageRating: Double?
)