import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.enums.BookingTab
import com.example.myapplication.presentation.component.BookingList
import com.example.myapplication.presentation.component.CompletedList
import com.example.myapplication.presentation.screen.profile.my_tickets.ProfileMyTicketsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMyTicketsScreen(
    viewModel: ProfileMyTicketsViewModel = hiltViewModel(),
    onNavigateToDetail: (bookingId: String) -> Unit,
    onNavigateBack: () -> Unit // Thêm prop này để back về
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử đặt vé", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0.dp) // Sửa lỗi khoảng trống thừa ở top
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ===== TAB ROW =====
            TabRow(
                selectedTabIndex = state.selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                BookingTab.values().forEach { tab ->
                    val tabName = when (tab) {
                        BookingTab.UPCOMING -> "Sắp chiếu"
                        BookingTab.ONGOING -> "Đang chiếu"
                        BookingTab.COMPLETED -> "Đã xem"
                    }

                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                text = tabName,
                                fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                }
            }

            // ===== CONTENT =====
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (state.selectedTab) {
                    BookingTab.UPCOMING -> {
                        if (state.isLoadingUpcoming) CircularProgressIndicator()
                        else BookingList(state.upcoming, onNavigateToDetail)
                    }
                    BookingTab.ONGOING -> {
                        if (state.isLoadingOngoing) CircularProgressIndicator()
                        else BookingList(state.ongoing, onNavigateToDetail)
                    }
                    BookingTab.COMPLETED -> {
                        if (state.isLoadingCompleted) CircularProgressIndicator()
                        else CompletedList(
                            bookings = state.completed,
                            onRate = viewModel::rateMovie,
                            loadingIds = state.ratingLoadingIds,
                            onNavigateToDetail = onNavigateToDetail
                        )
                    }
                }
            }
        }
    }
}