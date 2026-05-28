import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
    onNavigateBack: () -> Unit, // Thêm prop này để back về
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetState() // Xóa dữ liệu cũ
        viewModel.selectTab(BookingTab.UPCOMING) // Tự động load tab đầu
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lịch sử đặt vé",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
                // Ẩn đường kẻ mờ mặc định của TabRow để giao diện nhìn sạch và hiện đại hơn
                divider = {},
                // 🔥 Tùy chỉnh thanh gạch dưới (Indicator)
                indicator = { tabPositions ->
                    if (state.selectedTab.ordinal < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.ordinal]),
                            color = MaterialTheme.colorScheme.primary,
                            height = 4.dp // Tăng độ dày của thanh gạch dưới (Mặc định chỉ 2.dp)
                        )
                    }
                }
            ) {
                BookingTab.values().forEach { tab ->
                    val tabName = when (tab) {
                        BookingTab.UPCOMING -> "Sắp chiếu"
                        BookingTab.ONGOING -> "Đang chiếu"
                        BookingTab.COMPLETED -> "Đã xem"
                    }

                    val isSelected = state.selectedTab == tab

                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        // Phân biệt rõ ràng màu sắc khi chọn và không chọn
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = {
                            Text(
                                text = tabName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                // 🔥 Tăng cỡ chữ từ titleSmall lên titleMedium
                                style = MaterialTheme.typography.titleMedium
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