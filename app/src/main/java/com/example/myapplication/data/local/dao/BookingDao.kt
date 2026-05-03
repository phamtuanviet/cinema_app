package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    // Trả về Flow để UI tự động cập nhật khi DB thay đổi
    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY showtimeStart DESC")
    fun getBookingsByStatus(status: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: String): BookingEntity?


    // Dùng REPLACE: Nếu vé từ API trả về đã có trong Room -> ghi đè cập nhật mới
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Query("DELETE FROM bookings WHERE status = :status")
    suspend fun clearBookingsByStatus(status: String)

    @Query("DELETE FROM bookings WHERE status = :status")
    suspend fun deleteBookingsByStatus(status: String)
}