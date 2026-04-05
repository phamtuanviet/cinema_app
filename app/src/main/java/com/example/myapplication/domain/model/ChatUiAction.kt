package com.example.myapplication.domain.model

import com.example.myapplication.data.remote.dto.BookingHistoryResponse
import com.example.myapplication.data.remote.dto.CinemaNearbyResponse
import com.example.myapplication.data.remote.dto.MovieChatbotResponse
import com.example.myapplication.data.remote.dto.MovieDetailResponse
import com.example.myapplication.data.remote.dto.ShowtimeChatbotResponse
import com.example.myapplication.data.remote.dto.SpringPageResponse
import com.example.myapplication.data.remote.dto.UserPointsResponse
import com.example.myapplication.data.remote.dto.UserVoucherChatbotResponse

sealed class ChatUiAction {

    // 1. Nhóm Phim ảnh
    data class ShowMovieList(val data: SpringPageResponse<MovieChatbotResponse>) : ChatUiAction()
    data class NavigateToMovieDetail(val data: MovieDetailResponse) : ChatUiAction()

    // 2. Nhóm Rạp & Lịch chiếu
    data class ShowShowtimes(val data: List<ShowtimeChatbotResponse>) : ChatUiAction()
    data class ShowCinemaMap(val data: List<CinemaNearbyResponse>) : ChatUiAction()
    object RequestLocationPermission : ChatUiAction() // Object vì không cần mang theo data

    // 3. Nhóm Cá nhân hóa (User)
    data class ShowUserPoints(val data: UserPointsResponse) : ChatUiAction()
    data class ShowBookingHistory(val data: List<BookingHistoryResponse>) : ChatUiAction()
    data class ShowVoucherList(val data: List<UserVoucherChatbotResponse>) : ChatUiAction()

    // 4. Fallback (Phòng hờ khi backend Node.js trả về một type mới mà Android chưa kịp update)
    data class Unknown(val type: String) : ChatUiAction()
}