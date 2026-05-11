package com.example.myapplication.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectionDialog(
    initialLat: Double?,
    initialLng: Double?,
    onLocationSelected: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    // Toạ độ mặc định (Ví dụ: Trung tâm Việt Nam hoặc Hà Nội)
    val defaultLocation = LatLng(21.028511, 105.804817)

    // Nếu đã có toạ độ (chỉnh sửa) thì lấy, không thì lấy mặc định
    val startLocation = if (initialLat != null && initialLng != null) {
        LatLng(initialLat, initialLng)
    } else defaultLocation

    // State quản lý vị trí Camera và Vị trí của Ghim (Marker)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 12f)
    }
    var selectedLatLng by remember { mutableStateOf(startLocation) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Dialog Full màn hình
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chạm để chọn vị trí") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            onLocationSelected(selectedLatLng.latitude, selectedLatLng.longitude)
                        }) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Xác nhận")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // Bản đồ Google Maps
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false),
                    // KHI NGƯỜI DÙNG BẤM VÀO BẢN ĐỒ
                    onMapClick = { latLng ->
                        selectedLatLng = latLng
                    }
                ) {
                    // Hiển thị Ghim (Pin) tại vị trí đang chọn
                    Marker(
                        state = MarkerState(position = selectedLatLng),
                        title = "Vị trí Rạp",
                        snippet = "Toạ độ: ${selectedLatLng.latitude}, ${selectedLatLng.longitude}"
                    )
                }

                // Gợi ý nhỏ ở dưới cùng
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = "Kéo và chạm vào bản đồ để ghim vị trí chính xác",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}