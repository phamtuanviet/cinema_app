package com.example.myapplication.presentation.screen.admin.menu

import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

// Model dữ liệu cho mỗi item trong Menu
data class AdminMenuItem(
    val title: String,
    val icon: ImageVector,
    val iconTint: Color,
    val containerColor: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMoreMenuScreen(
    viewModel: AdminMoreMenuViewModel = hiltViewModel(), // Nhúng ViewModel
    onNavigateToUsers: () -> Unit,
    onNavigateToCinemas: () -> Unit,
    onNavigateToShowtimes: () -> Unit,
    onNavigateToMovies: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToCombos: () -> Unit,
    onNavigateToVouchers: () -> Unit,
    onNavigateToBanners : () -> Unit,
    // 🔥 THÊM 2 CALLBACK CHO TIN TỨC VÀ ĐIỂM
    onNavigateToSettings: () -> Unit,
    onNavigateToRevenue: () -> Unit,
    onNavigateToNews: () -> Unit,
    onLogoutSuccess: () -> Unit // Callback khi logout xong
) {
    val state by viewModel.state.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Theo dõi trạng thái Logout
    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    // Dialog xác nhận đăng xuất
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Đăng xuất Admin") },
            text = { Text(text = "Bạn có chắc chắn muốn thoát quyền quản trị không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.onLogoutClick()
                    }
                ) {
                    Text("Đăng xuất", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Danh sách menu đã được bổ sung
    val menuItems = listOf(
        AdminMenuItem("Doanh Thu", Icons.Default.Insights, Color(0xFF43A047), Color(0xFFE8F5E9), onNavigateToRevenue),
        AdminMenuItem("Quản lý Phim", Icons.Default.Movie, Color(0xFFE53935), Color(0xFFFFEBEE), onNavigateToMovies),
        AdminMenuItem("Quản lý Rạp", Icons.Default.Domain, Color(0xFF1E88E5), Color(0xFFE3F2FD), onNavigateToCinemas),
        AdminMenuItem("Lịch Chiếu", Icons.Default.DateRange, Color(0xFF8E24AA), Color(0xFFF3E5F5), onNavigateToShowtimes),
        AdminMenuItem("Người Dùng", Icons.Default.Group, Color(0xFF00897B), Color(0xFFE0F2F1), onNavigateToUsers),
        AdminMenuItem("Đơn Vé (Booking)", Icons.Default.Receipt, Color(0xFFFF8F00), Color(0xFFFFF8E1), onNavigateToBookings),
        AdminMenuItem("Combo Bắp Nước", Icons.Default.Fastfood, Color(0xFFF4511E), Color(0xFFFBE9E7), onNavigateToCombos),
        AdminMenuItem("Khuyến Mãi (Voucher)", Icons.Default.LocalOffer, Color(0xFFE91E63), Color(0xFFFCE4EC), onNavigateToVouchers),
        // 🔥 THÊM 2 MỤC MỚI VÀO ĐÂY
        AdminMenuItem("Tin Tức", Icons.Default.Article, Color(0xFF546E7A), Color(0xFFECEFF1), onNavigateToNews),
        AdminMenuItem("Quản lý Banner", Icons.Default.ViewCarousel, Color(0xFFFB8C00), Color(0xFFFFF3E0), onNavigateToBanners),
        AdminMenuItem("Cài đặt", Icons.Default.Settings, Color(0xFF607D8B), Color(0xFFCFD8DC), onNavigateToSettings))

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Menu Quản trị",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
    )  { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AdminProfileHeader()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(menuItems) { item ->
                        AdminMenuCard(item)
                    }
                }

                // Nút Đăng Xuất mở Dialog
                Button(
                    onClick = { showLogoutDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
            }

            // Hiển thị vòng xoay loading đè lên tất cả khi đang gọi API Logout
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// ================= CÁC COMPONENT PHỤ =================

@Composable
fun AdminProfileHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar giả lập
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Admin Avatar",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "Xin chào, Super Admin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Quản lý toàn bộ hệ thống rạp",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuCard(item: AdminMenuItem) {
    Card(
        onClick = item.onClick, // Chuẩn MD3: Dùng onClick trực tiếp trên Card tạo Ripple effect mượt mà
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            // 1. CỐ ĐỊNH CHIỀU CAO CHO TẤT CẢ CÁC THẺ
            // Hoặc bạn có thể dùng .aspectRatio(1f) nếu muốn thẻ vuông chằn chặn 100%
            .height(136.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // 2. CĂN GIỮA TOÀN BỘ NỘI DUNG THEO CHIỀU DỌC
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                // 3. Bắt buộc Column phải chiếm toàn bộ 136.dp chiều cao của Card
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(item.containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                // 4. KIỂM SOÁT TEXT
                maxLines = 2, // Chỉ cho phép tối đa 2 dòng
                minLines = 2, // Ép luôn luôn chiếm không gian của 2 dòng (giúp các Icon thẳng hàng nhau tuyệt đối)
                overflow = TextOverflow.Ellipsis // Nếu chữ dài quá 2 dòng thì hiển thị "..."
            )
        }
    }
}