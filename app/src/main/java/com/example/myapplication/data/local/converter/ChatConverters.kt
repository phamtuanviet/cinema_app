package com.example.myapplication.data.local.converter

import androidx.room.TypeConverter
import com.example.myapplication.data.remote.dto.RawUiAction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatConverters {
    private val gson = Gson()

    // Chuyển từ List (Lúc lấy từ API) -> String (Để lưu vào DB)
    @TypeConverter
    fun fromUiActionList(value: List<RawUiAction>?): String? {
        if (value == null) return null
        return gson.toJson(value)
    }

    // Chuyển từ String (Lúc lấy từ DB ra) -> List (Để hiển thị lên UI)
    @TypeConverter
    fun toUiActionList(value: String?): List<RawUiAction>? {
        if (value == null) return null
        val type = object : TypeToken<List<RawUiAction>>() {}.type
        return gson.fromJson(value, type)
    }
}