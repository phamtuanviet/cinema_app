package com.example.myapplication.utils

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign


@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5) // Nền hơi xám để dễ nhìn bóng
@Composable
fun ElevationComparisonPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. CHỈ DÙNG TONAL ELEVATION (Đặc sản của Material 3)
            Surface(
                modifier = Modifier.size(160.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 16.dp, // 🔥 Tăng độ cao Tonal
                shadowElevation = 0.dp  // Tắt bóng
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Tonal Elevation\n\nKhông có bóng đổ.\nNền bị ám màu Primary của hệ thống.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 2. CHỈ DÙNG SHADOW MẶC ĐỊNH
            Surface(
                modifier = Modifier.size(160.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,   // Tắt ám màu
                shadowElevation = 16.dp  // 🔥 Chỉ dùng bóng mặc định
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Shadow Mặc định\n\nCó bóng đổ nhưng khá gắt và đậm.\nMàu nền giữ nguyên.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 3. CUSTOM SHADOW (Cách bạn đang dùng)
            Surface(
                modifier = Modifier
                    .size(160.dp)
                    .fillMaxWidth()
                    // 🔥 Tự vẽ bóng bằng Modifier
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background, // Màu nền sạch
                tonalElevation = 0.dp,  // Tắt ám màu
                shadowElevation = 0.dp  // Tắt bóng mặc định (vì đã tự vẽ ở Modifier)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Custom Shadow\n(Code của bạn)\n\nBóng đổ cực kỳ mềm mại, tản đều và sang trọng.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true, name = "1. Navigation & Actions")
//@Composable
//fun NavigationActionsPreview() {
//    var selectedItem by remember { mutableStateOf(0) }
//    val items = listOf("Home", "Movies", "Profile")
//    val icons = listOf(Icons.Filled.Home, Icons.Filled.PlayArrow, Icons.Filled.Person)
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .height(300.dp)
//    ) {
//        // Cấu trúc một màn hình giả định có NavigationBar ở đáy
//        Column(modifier = Modifier.fillMaxSize()) {
//            Text(
//                text = "Màn hình chính ứng dụng",
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.padding(16.dp)
//            )
//            Spacer(modifier = Modifier.weight(1f))
//
//            NavigationBar {
//                items.forEachIndexed { index, item ->
//                    NavigationBarItem(
//                        icon = { Icon(icons[index], contentDescription = item) },
//                        label = { Text(item) },
//                        selected = selectedItem == index,
//                        onClick = { selectedItem = index }
//                    )
//                }
//            }
//        }
//
//        // FAB nổi vuông bo góc lớn đặc trưng M3 nằm đè lên góc phải
//        FloatingActionButton(
//            onClick = { },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(end = 16.dp, bottom = 96.dp) // Đẩy lên trên NavigationBar
//        ) {
//            Icon(Icons.Filled.Add, contentDescription = "Add")
//        }
//    }
//}
//
//@Preview(showBackground = true, name = "2. Material 3 Buttons Hierarchy")
//@Composable
//fun ButtonsPreview() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Thứ tự ưu tiên các nút trong M3", style = MaterialTheme.typography.titleMedium)
//
//        Button(onClick = { /* Xử lý khi click */ }) {
//            Text("Filled Button (Ưu tiên Cao nhất)")
//        }
//
//        FilledTonalButton(onClick = {}) {
//            Text("Filled Tonal Button (Ưu tiên Vừa)")
//        }
//
//        ElevatedButton(onClick = {}) {
//            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(18.dp))
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Elevated Button (Có bóng đổ)")
//        }
//
//        OutlinedButton(onClick = {}) {
//            Text("Outlined Button (Ưu tiên Thấp)")
//        }
//
//        TextButton(onClick = {}) {
//            Text("Text Button (Ưu tiên Thấp nhất)")
//        }
//    }
//}
//
//@Preview(showBackground = true, name = "3. Containers & Lists")
//@Composable
//fun ContainersPreview() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text("Khay chứa vật lý (Containers)", style = MaterialTheme.typography.titleMedium)
//
//        // M3 Card - sử dụng phân tầng màu thay cho đổ bóng nặng
//        Card(
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.surfaceVariant
//            ),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text("Độ tuổi: 20 tuổi", style = MaterialTheme.typography.titleLarge)
//                Text("Fourth-year IT Student", style = MaterialTheme.typography.bodyMedium)
//            }
//        }
//
//        // ListItem chuẩn hóa cho các dòng Settings/Danh sách
//        Surface(
//            tonalElevation = 2.dp,
//            shape = MaterialTheme.shapes.medium
//        ) {
//            ListItem(
//                headlineContent = { Text("Thông báo hệ thống") },
//                supportingContent = { Text("VNPAY sandbox testing completed.") },
//                leadingContent = {
//                    Icon(
//                        Icons.Filled.Notifications,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                },
//                trailingContent = { Text("10:45") }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true, name = "4. Inputs & Selection")
//@Composable
//fun InputsPreview() {
//    var text by remember { mutableStateOf("") }
//    var checked by remember { mutableStateOf(true) }
//    var switchOn by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text("Nhập liệu và Lựa chọn", style = MaterialTheme.typography.titleMedium)
//
//        OutlinedTextField(
//            value = text,
//            onValueChange = { text = it },
//            label = { Text("Nhập Email đăng nhập") },
//            placeholder = { Text("example@gmail.com") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Checkbox(checked = checked, onCheckedChange = { checked = it })
//                Text("Ghi nhớ tài khoản")
//            }
//
//            // Switch M3 có con chạy (thumb) lớn hơn hẳn M2
//            Switch(checked = switchOn, onCheckedChange = { switchOn = it })
//        }
//
//        // Filter Chips thường dùng chọn thể loại phim/tag
//        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            FilterChip(
//                selected = true,
//                onClick = {},
//                label = { Text("Hành động") },
//                leadingIcon = { Icon(Icons.Filled.Check, modifier = Modifier.size(16.dp), contentDescription = null) }
//            )
//            FilterChip(
//                selected = false,
//                onClick = {},
//                label = { Text("Viễn tưởng") }
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true, name = "5. Feedback & Dialogs")
//@Composable
//fun FeedbackPreview() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .height(260.dp)
//    ) {
//        // Mô phỏng cấu trúc AlertDialog của M3 (Bo góc cực lớn 28.dp)
//        AlertDialog(
//            onDismissRequest = {},
//            confirmButton = {
//                TextButton(onClick = {}) { Text("Đồng ý") }
//            },
//            dismissButton = {
//                TextButton(onClick = {}) { Text("Hủy") }
//            },
//            title = { Text("Xác nhận hủy đặt vé?") },
//            text = { Text("Hành động này không thể hoàn tác, ghế số A12 của bạn sẽ được giải phóng ngay lập tức.") },
//            modifier = Modifier.align(Alignment.TopCenter)
//        )
//
//        // Snackbar thông báo nhanh phía đáy
//        Snackbar(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(8.dp),
//            action = {
//                TextButton(onClick = {}, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.inversePrimary)) {
//                    Text("HOÀN TÁC")
//                }
//            }
//        ) {
//            Text("Đã thêm phim vào danh sách yêu thích.")
//        }
//    }
//}
//
//@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun MovieAppColorSchemePreview() {
//    // Để chạy thực tế, bọc màn hình này trong Theme của App bạn (ví dụ: MyMovieAppTheme)
//    // Ở đây dùng MaterialTheme mặc định của hệ thống để minh họa
//    MaterialTheme {
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            // 1. Dùng 'background' cho tấm nền lớn dưới cùng của toàn bộ màn hình
//            color = MaterialTheme.colorScheme.background
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                // --- THANH TÌM KIẾM ---
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(48.dp)
//                        .clip(RoundedCornerShape(24.dp))
//                        // 2. Dùng 'surfaceContainerHighest' cho thanh tìm kiếm để nó nổi bật nhất
//                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
//                        .padding(horizontal = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        Icons.Default.Search,
//                        contentDescription = null,
//                        // 3. Icon nằm trên nền surfaceContainer nên dùng 'onSurfaceVariant'
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "Tìm kiếm phim, rạp chiếu...",
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//
//                // --- KHỐI THÔNG TIN BÀI VIẾT / VÉ PHIM (CARD) ---
//                // 4. Dùng 'surfaceContainer' làm phông nền cho thẻ chứa thông tin phim
//                Card(
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.surfaceContainer
//                    ),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        // Text trên nền Surface -> dùng 'onSurface'
//                        Text(
//                            text = "Đã hoàn thành thanh toán",
//                            color = MaterialTheme.colorScheme.onSurface,
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text(
//                            text = "Vé xem phim: Doctor Strange",
//                            color = MaterialTheme.colorScheme.onSurfaceVariant,
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        // Nút bấm hành động chính trong Card
//                        // 5. 'Button' mặc định bú màu 'primary', chữ bên trong bú màu 'onPrimary'
//                        Button(
//                            onClick = {},
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("Xem mã vé (QR Code)")
//                        }
//                    }
//                }
//
//                // --- BANNER THÔNG BÁO ƯU ĐÃI (DÙNG COLOR CONTAINER) ---
//                // 6. Dùng 'primaryContainer' làm nền cho cả một cụm nội dung cần gây chú ý đặc biệt
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(12.dp))
//                        .background(MaterialTheme.colorScheme.primaryContainer)
//                        .padding(12.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        Icons.Default.Info,
//                        contentDescription = null,
//                        // Chữ/Icon trên nền primaryContainer -> dùng 'onPrimaryContainer'
//                        tint = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Text(
//                        text = "Ưu đãi tuần này: Giảm 20% khi thanh toán qua ví điện tử.",
//                        color = MaterialTheme.colorScheme.onPrimaryContainer,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//
//                // --- ĐƯỜNG KẺ PHÂN CÁCH VÀ NÚT PHỤ ---
//                // 7. Dùng 'outlineVariant' làm đường kẻ chia cắt giao diện mảnh và mờ nhẹ
//                HorizontalDivider(
//                    modifier = Modifier.fillMaxWidth(),
//                    thickness = 1.dp,
//                    color = MaterialTheme.colorScheme.outlineVariant
//                )
//
//                // 8. Dùng 'OutlinedButton' có đường viền 'outline' để làm hành động phụ
//                OutlinedButton(
//                    onClick = {},
//                    modifier = Modifier.fillMaxWidth(),
//                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
//                ) {
//                    Text("Hủy đặt vé", color = MaterialTheme.colorScheme.primary)
//                }
//
//                // --- SNACKBAR (MÀU ĐẢO NGƯỢC INVERSE) ---
//                // 9. Dùng 'inverseSurface' và 'inverseOnSurface' để snackbar tự động đổi màu giật mình
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(4.dp))
//                        .background(MaterialTheme.colorScheme.inverseSurface)
//                        .padding(horizontal = 16.dp, vertical = 10.dp)
//                ) {
//                    Text(
//                        text = "Đã sao chép mã giao dịch thành công.",
//                        color = MaterialTheme.colorScheme.inverseOnSurface,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//        }
//    }
//}