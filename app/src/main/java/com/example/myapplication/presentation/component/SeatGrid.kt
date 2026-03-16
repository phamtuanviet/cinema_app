import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun SeatGrid(
    rows: List<com.example.myapplication.data.remote.dto.SeatRowDto>,
    selectedSeats: Set<String>,
    onSeatClick: (String) -> Unit
) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        rows.forEach { row ->

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                row.seats.forEach { seat ->

                    val color = when (seat.status) {

                        SeatStatus.AVAILABLE -> Color.LightGray

                        SeatStatus.HELD_BY_ME -> Color(0xFF1E88E5)

                        SeatStatus.HELD_BY_OTHER -> Color.Red

                        SeatStatus.BOOKED -> Color.DarkGray
                    }

                    IconButton(
                        onClick = { onSeatClick(seat.id) }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.EventSeat,
                            contentDescription = null,
                            tint = color
                        )
                    }
                }
            }
        }
    }
}