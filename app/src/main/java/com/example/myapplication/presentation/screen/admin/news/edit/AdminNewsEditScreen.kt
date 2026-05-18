package com.example.myapplication.presentation.screen.admin.news.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.presentation.screen.admin.voucher.edit.SelectableChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNewsEditScreen(
    newsId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminNewsEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showVoucherDropdown by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        viewModel.onEvent(NewsEditEvent.ImageSelected(it))
    }

    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onSaveSuccess() }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = { Text("Chỉnh sửa Bài viết", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            if (!state.isLoadingData) {
                Button(
                    onClick = { viewModel.onEvent(NewsEditEvent.SaveClicked(context)) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else Text("Cập nhật bài viết")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoadingData) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- THUMBNAIL ---
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    contentAlignment = Alignment.Center
                ) {
                    val img = state.selectedImageUri ?: state.existingThumbnailUrl
                    if (img != null) {
                        AsyncImage(model = img, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.AddPhotoAlternate, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // --- TIÊU ĐỀ & LOẠI ---
                OutlinedTextField(value = state.title, onValueChange = { viewModel.onEvent(NewsEditEvent.TitleChanged(it)) }, label = { Text("Tiêu đề bài viết (*)") }, modifier = Modifier.fillMaxWidth())

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SelectableChip(text = "Tin tức (Normal)", isSelected = state.type == "NORMAL", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(NewsEditEvent.TypeChanged("NORMAL")) })
                    SelectableChip(text = "Khuyến mãi (Voucher)", isSelected = state.type == "VOUCHER", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(NewsEditEvent.TypeChanged("VOUCHER")) })
                }

                // --- NẾU LÀ LOẠI VOUCHER -> HIỆN CHỌN VOUCHER ---
                if (state.type == "VOUCHER") {
                    Box(Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.availableVouchers.find { it.id == state.selectedVoucherId }?.code ?: "Chọn Voucher đính kèm...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Voucher áp dụng") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { IconButton(onClick = { showVoucherDropdown = true }) { Icon(Icons.Default.ArrowDropDown, null) } }
                        )
                        DropdownMenu(expanded = showVoucherDropdown, onDismissRequest = { showVoucherDropdown = false }) {
                            state.availableVouchers.forEach { v ->
                                DropdownMenuItem(text = { Text(v.code) }, onClick = { viewModel.onEvent(NewsEditEvent.VoucherSelected(v.id)); showVoucherDropdown = false })
                            }
                        }
                    }
                }

                // --- NỘI DUNG VĂN BẢN (TEXT AREA) ---
                OutlinedTextField(
                    value = state.content,
                    onValueChange = { viewModel.onEvent(NewsEditEvent.ContentChanged(it)) },
                    label = { Text("Nội dung bài viết") },
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    placeholder = { Text("Nhập nội dung chi tiết bài viết tại đây...") }
                )

                // --- THỜI GIAN & TRẠNG THÁI ---
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.startDateStr, onValueChange = { viewModel.onEvent(NewsEditEvent.StartDateChanged(it)) }, label = { Text("Ngày bắt đầu") }, modifier = Modifier.weight(1f), placeholder = { Text("yyyy-mm-dd hh:mm") })
                    OutlinedTextField(value = state.endDateStr, onValueChange = { viewModel.onEvent(NewsEditEvent.EndDateChanged(it)) }, label = { Text("Ngày kết thúc") }, modifier = Modifier.weight(1f), placeholder = { Text("yyyy-mm-dd hh:mm") })
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Trạng thái: ${if(state.published) "Công khai" else "Bản nháp"}", fontWeight = FontWeight.Bold)
                    Switch(checked = state.published, onCheckedChange = { viewModel.onEvent(NewsEditEvent.PublishedChanged(it)) })
                }

                Spacer(Modifier.height(100.dp))
            }
        }
    }
}