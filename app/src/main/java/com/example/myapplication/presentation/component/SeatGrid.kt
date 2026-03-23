import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.remote.dto.SeatRowDto
import com.example.myapplication.data.remote.enums.SeatStatus
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.enums.SeatType
import com.example.myapplication.presentation.component.CoupleSeatItem
import com.example.myapplication.presentation.component.SingleSeatItem

@Composable
fun SeatGrid(
    rows: List<com.example.myapplication.data.remote.dto.SeatRowDto>,
    selectedSeats: List<String>,
    onSeatClick: (List<String>) -> Unit // Đổi thành List<String> để chọn cùng lúc 2 ghế nếu là ghế đôi
) {

    val verticalScrollState = rememberScrollState()
    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(verticalScrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                var index = 0
                while (index < row.seats.size) {
                    val seat = row.seats[index]

                    // Giả sử model của bạn có thuộc tính seatType == "COUPLE"
                    val isCouple = seat.seatType == SeatType.COUPLE && (index + 1 < row.seats.size)

                    if (isCouple) {
                        val nextSeat = row.seats[index + 1]
                        CoupleSeatItem(
                            status1 = seat.status,
                            status2 = nextSeat.status,
                            onClick = {
                                // Gửi lên cả 2 ID để ViewModel toggle cả 2 ghế
                                onSeatClick(listOf(seat.id, nextSeat.id))
                            }
                        )
                        index += 2 // Bỏ qua ghế tiếp theo vì đã gộp
                    } else {
                        SingleSeatItem(
                            status = seat.status,
                            onClick = {
                                onSeatClick(listOf(seat.id))
                            }
                        )
                        index++
                    }

                    // Khoảng cách giữa các khối ghế
                    if (index < row.seats.size) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}