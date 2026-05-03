package com.example.myapplication.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.local.converter.ChatConverters
import com.example.myapplication.data.local.dao.BookingDao
import com.example.myapplication.data.local.dao.ChatDao
import com.example.myapplication.data.local.entity.BookingEntity
import com.example.myapplication.data.local.entity.ChatMessageEntity

@Database(
    entities = [ChatMessageEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ChatConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    abstract fun bookingDao(): BookingDao
}