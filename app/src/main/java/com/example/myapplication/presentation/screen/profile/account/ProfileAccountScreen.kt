package com.example.myapplication.presentation.screen.profile.account



import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.presentation.component.CameraPreview
import com.example.myapplication.utils.uriToFile

// Import các hàm uriToFile của bạn tại đây...

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAccountScreen(
    viewModel: ProfileAccountViewModel = hiltViewModel(),
    onNavigateToChangePassword: () -> Unit,
    onNavigateBack: () -> Unit // Thêm hàm điều hướng quay lại
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // 1. Khai báo FocusManager để quản lý bàn phím
    val focusManager = LocalFocusManager.current

    var showPicker by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }

    // ===== Permission & Gallery Launchers giữ nguyên =====
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) showCamera = true }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.setAvatar(file)
        }
    }

    if (showCamera) {
        CameraPreview(
            onImageCaptured = { file -> viewModel.setAvatar(file); showCamera = false },
            onClose = { showCamera = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tài khoản", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
                ,windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                // 3. Bắt sự kiện chạm ra vùng trống để ẩn bàn phím/bỏ focus
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Khung Avatar có thể bấm =====
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { showPicker = true },
                contentAlignment = Alignment.BottomEnd
            ) {
                // Ảnh Avatar
                val avatarPainter = if (state.avatarFile != null) {
                    rememberAsyncImagePainter(state.avatarFile)
                } else if (state.user?.avatarUrl != null) {
                    rememberAsyncImagePainter(state.user!!.avatarUrl)
                } else {
                    null // Dùng icon mặc định
                }

                if (avatarPainter != null) {
                    Image(
                        painter = avatarPainter,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Nút / Icon Camera nhỏ ở góc (Badge)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoCamera,
                        contentDescription = "Đổi ảnh",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ===== Form nhập liệu =====
            OutlinedTextField(
                value = state.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = { Text("Họ và tên") },
                leadingIcon = {
                    Icon(Icons.Rounded.Badge, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::updateProfile,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isUpdating,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Cập nhật thông tin", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateToChangePassword,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đổi mật khẩu")
            }

            // Hiển thị thông báo lỗi nếu có
            state.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Hiển thị thông báo thành công (tuỳ chọn)
            if (state.updateSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cập nhật thành công!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                // Lưu ý: Thường nên gọi viewModel.clearFlags() sau khi show xong
            }
        }
    }

    // ===== Bottom Sheet Chọn Ảnh =====
    if (showPicker) {
        ModalBottomSheet(
            onDismissRequest = { showPicker = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 8.dp)
            ) {
                Text(
                    text = "Chọn ảnh đại diện",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Lựa chọn Chụp ảnh
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showPicker = false
                            val granted = ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (granted) {
                                showCamera = true
                            } else {
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Chụp ảnh mới", style = MaterialTheme.typography.bodyLarge)
                }

                // Lựa chọn Thư viện
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showPicker = false
                            galleryLauncher.launch("image/*")
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Chọn từ thư viện", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}