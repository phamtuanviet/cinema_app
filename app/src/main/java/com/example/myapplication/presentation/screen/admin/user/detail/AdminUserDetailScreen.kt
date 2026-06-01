package com.example.myapplication.presentation.screen.admin.user.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.AdminLoyaltyTransactionDto
import com.example.myapplication.data.remote.dto.AdminUserVoucherDto
import com.example.myapplication.data.remote.dto.UserDetailTab
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetailScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: AdminUserDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chi tiết người dùng",
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
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Thêm nếu sau này bạn có nút Action bên phải
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) { CircularProgressIndicator() }
        } else if (state.error != null || state.userDetail == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) { Text(state.error ?: "Không tìm thấy user", color = MaterialTheme.colorScheme.error) }
        } else {
            val user = state.userDetail!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // --- 1. HEADER PROFILE ---
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = user.avatarUrl ?: "https://picsum.photos/200/200",
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(user.fullName ?: "Chưa cập nhật tên", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(user.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // --- 2. TAB MENU ---
                val tabs = UserDetailTab.values()
                val selectedTabIndex = tabs.indexOf(state.currentTab)

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEach { tab ->
                        Tab(
                            selected = state.currentTab == tab,
                            onClick = { viewModel.onTabSelected(tab) },
                            text = { Text(tab.title, fontWeight = if (state.currentTab == tab) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                // --- 3. NỘI DUNG TỪNG TAB ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (state.currentTab) {
                        UserDetailTab.VOUCHER -> {
                            if (user.userVouchers.isEmpty()) {
                                item { Text("Người dùng chưa sở hữu Voucher nào.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            } else {
                                items(user.userVouchers, key = { it.id }) { voucher ->
                                    UserVoucherItem(voucher)
                                }
                            }
                        }
                        UserDetailTab.LOYALTY -> {
                            // Cục hiển thị Điểm hiện tại
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.Stars, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Điểm thưởng khả dụng", style = MaterialTheme.typography.titleMedium)
                                        Text("${user.availablePoints} Điểm", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }
                                }
                                Text("Lịch sử giao dịch điểm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                            }

                            if (user.loyaltyTransactions.isEmpty()) {
                                item { Text("Chưa có giao dịch điểm nào.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            } else {
                                items(user.loyaltyTransactions, key = { it.id }) { transaction ->
                                    LoyaltyTransactionItem(transaction)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ITEM VIEW: VOUCHER CỦA USER ---
@Composable
fun UserVoucherItem(voucher: AdminUserVoucherDto) {
    val formatMoney = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val discountStr = if (voucher.discountType == "PERCENT") "Giảm ${voucher.discountValue}%" else "Giảm ${formatMoney.format(voucher.discountValue)}"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalOffer, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(voucher.voucherCode, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(discountStr, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                if (voucher.isUsed) {
                    Text("Đã dùng: ${voucher.usedAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Badge trạng thái
            val badgeColor = if (voucher.isUsed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
            val badgeTextColor = if (voucher.isUsed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
            Surface(color = badgeColor, shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = if (voucher.isUsed) "ĐÃ DÙNG" else "CHƯA DÙNG",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeTextColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- ITEM VIEW: LỊCH SỬ ĐIỂM CỦA USER ---
@Composable
fun LoyaltyTransactionItem(transaction: AdminLoyaltyTransactionDto) {
    val isEarned = transaction.points > 0
    val amountColor = if (isEarned) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val sign = if (isEarned) "+" else ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isEarned) Icons.Default.AddCircle else Icons.Default.RemoveCircle,
                contentDescription = null,
                tint = amountColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.description, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(transaction.createdAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "$sign${transaction.points}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = amountColor
            )
        }
    }
}