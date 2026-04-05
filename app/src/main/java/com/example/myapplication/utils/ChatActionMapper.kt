package com.example.myapplication.util // Hoặc package mapper tùy bạn

import com.example.myapplication.data.remote.dto.BookingHistoryResponse
import com.example.myapplication.data.remote.dto.CinemaNearbyResponse
import com.example.myapplication.data.remote.dto.MovieChatbotResponse
import com.example.myapplication.data.remote.dto.MovieDetailResponse
import com.example.myapplication.data.remote.dto.RawUiAction
import com.example.myapplication.data.remote.dto.ShowtimeChatbotResponse
import com.example.myapplication.data.remote.dto.SpringPageResponse
import com.example.myapplication.data.remote.dto.UserPointsResponse
import com.example.myapplication.data.remote.dto.UserVoucherChatbotResponse
import com.example.myapplication.domain.model.ChatUiAction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class ChatActionMapper @Inject constructor(
    private val gson: Gson
) {
    fun map(rawAction: RawUiAction): ChatUiAction {
        // Nếu thiếu data và không phải là action xin quyền GPS, trả về Unknown
        if (rawAction.data == null && rawAction.type != "REQUEST_LOCATION_PERMISSION") {
            return ChatUiAction.Unknown(rawAction.type)
        }

        return try {
            when (rawAction.type) {
                // ÉP KIỂU SANG DẠNG PAGE (Object)
                "SHOW_MOVIE_LIST" -> {
                    val typeToken = object : TypeToken<SpringPageResponse<MovieChatbotResponse>>() {}.type
                    ChatUiAction.ShowMovieList(gson.fromJson(rawAction.data, typeToken))
                }

                // ÉP KIỂU SANG OBJECT ĐƠN
                "NAVIGATE_TO_MOVIE_DETAIL" -> {
                    ChatUiAction.NavigateToMovieDetail(gson.fromJson(rawAction.data, MovieDetailResponse::class.java))
                }
                "SHOW_USER_POINTS" -> {
                    ChatUiAction.ShowUserPoints(gson.fromJson(rawAction.data, UserPointsResponse::class.java))
                }

                // ÉP KIỂU SANG DẠNG LIST (Array)
                "SHOW_SHOWTIMES" -> {
                    val typeToken = object : TypeToken<List<ShowtimeChatbotResponse>>() {}.type
                    ChatUiAction.ShowShowtimes(gson.fromJson(rawAction.data, typeToken))
                }
                "SHOW_CINEMA_MAP" -> {
                    val typeToken = object : TypeToken<List<CinemaNearbyResponse>>() {}.type
                    ChatUiAction.ShowCinemaMap(gson.fromJson(rawAction.data, typeToken))
                }
                "SHOW_BOOKING_HISTORY" -> {
                    val typeToken = object : TypeToken<List<BookingHistoryResponse>>() {}.type
                    ChatUiAction.ShowBookingHistory(gson.fromJson(rawAction.data, typeToken))
                }
                "SHOW_VOUCHER_LIST" -> {
                    val typeToken = object : TypeToken<List<UserVoucherChatbotResponse>>() {}.type
                    ChatUiAction.ShowVoucherList(gson.fromJson(rawAction.data, typeToken))
                }

                // CÁC TRƯỜNG HỢP ĐẶC BIỆT
                "REQUEST_LOCATION_PERMISSION" -> {
                    ChatUiAction.RequestLocationPermission
                }

                // LỌT SÀNG XUỐNG NIA (Type không khớp)
                else -> ChatUiAction.Unknown(rawAction.type)
            }
        } catch (e: Exception) {
            // NẾU GSON ÉP KIỂU THẤT BẠI (Do backend trả thiếu trường hoặc sai định dạng)
            // Bắt lỗi ở đây giúp App KHÔNG BỊ CRASH
            e.printStackTrace()
            ChatUiAction.Unknown(rawAction.type)
        }
    }
}