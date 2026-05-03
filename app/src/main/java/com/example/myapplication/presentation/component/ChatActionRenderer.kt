package com.example.myapplication.presentation.component



import androidx.compose.runtime.Composable

import androidx.navigation.NavController
import com.example.myapplication.domain.model.ChatUiAction

@Composable
fun ChatActionRenderer(
    action: ChatUiAction,
    navController: NavController,
    onLocationRequest: () -> Unit
) {
    when (action) {
        is ChatUiAction.NavigateToMovieDetail -> {
            val movie = action.data // MovieDetailResponse
            MovieDetailSnippetWidget(
                movie = movie,
                onClick = {
                    navController.navigate("movie_booking/${movie.movieId}")
                }
            )
        }

        is ChatUiAction.ShowMovieList -> {
            val movies = action.data.content ?: emptyList()
            MovieCarouselWidget(movies) { movieId ->
                navController.navigate("movie_booking/$movieId")
            }
        }

        // 3. LỊCH CHIẾU (SHOW_SHOWTIMES)
        is ChatUiAction.ShowShowtimes -> {
            ShowtimeListWidget(action.data) { showtimeId, movieId ->
                // Chuyển đến màn hình chọn ghế
                navController.navigate("booking_seat_selection/$showtimeId/$movieId")
            }
        }

        // 4. BẢN ĐỒ RẠP (SHOW_CINEMA_MAP)
        is ChatUiAction.ShowCinemaMap -> {
            CinemaListWidget(action.data) { cinemaId ->
                navController.navigate("cinema_detail/$cinemaId")
            }
        }

        // 5. ĐIỂM & VOUCHER (SHOW_USER_POINTS / SHOW_VOUCHER_LIST)
        is ChatUiAction.ShowUserPoints -> {
            // Widget chuyên biệt cho Điểm thưởng
            UserPointsWidget(action.data) {
                navController.navigate("voucher_list")
            }
        }

        is ChatUiAction.ShowVoucherList -> {
            // Widget chuyên biệt cho danh sách Voucher
            VoucherListWidget(action.data) {
                navController.navigate("voucher_list")
            }
        }

        // 6. LỊCH SỬ ĐẶT VÉ (SHOW_BOOKING_HISTORY)
        is ChatUiAction.ShowBookingHistory -> {
            BookingHistoryWidget(action.data) { bookingId ->
                navController.navigate("ticket_detail/$bookingId")
            }
        }

        // 7. XIN QUYỀN VỊ TRÍ
        is ChatUiAction.RequestLocationPermission -> {
            LocationRequestWidget(onLocationRequest)
        }

        else -> {}
    }
}