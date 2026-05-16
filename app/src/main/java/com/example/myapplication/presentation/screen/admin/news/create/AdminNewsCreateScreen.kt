package com.example.myapplication.presentation.screen.admin.news.create

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
import com.example.myapplication.presentation.screen.admin.news.edit.NewsEditEvent
import com.example.myapplication.presentation.screen.admin.voucher.edit.SelectableChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNewsCreateScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminNewsCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showVoucherDropdown by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        viewModel.onEvent(NewsCreateEvent.ImageSelected(it))
    }

    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onSaveSuccess() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo Bài viết Mới", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.onEvent(NewsCreateEvent.SaveClicked(context)) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Phát hành tin tức", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- THUMBNAIL PICKER ---
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedImageUri != null) {
                    AsyncImage(model = state.selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Nhấn để tải ảnh bìa", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // --- FORM INPUTS ---
            OutlinedTextField(value = state.title, onValueChange = { viewModel.onEvent(NewsCreateEvent.TitleChanged(it)) }, label = { Text("Tiêu đề bài viết (*)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableChip(text = "Tin tức", isSelected = state.type == "NORMAL", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(NewsCreateEvent.TypeChanged("NORMAL")) })
                SelectableChip(text = "Voucher", isSelected = state.type == "VOUCHER", modifier = Modifier.weight(1f), onClick = { viewModel.onEvent(NewsCreateEvent.TypeChanged("VOUCHER")) })
            }

            if (state.type == "VOUCHER") {
                Box(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.availableVouchers.find { it.id == state.selectedVoucherId }?.code ?: "Nhấn để chọn Voucher đính kèm...",
                        onValueChange = {}, readOnly = true, label = { Text("Voucher áp dụng") }, modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { IconButton(onClick = { showVoucherDropdown = true }) { Icon(Icons.Default.ArrowDropDown, null) } }
                    )
                    DropdownMenu(expanded = showVoucherDropdown, onDismissRequest = { showVoucherDropdown = false }) {
                        state.availableVouchers.forEach { v ->
                            DropdownMenuItem(text = { Text(v.code) }, onClick = { viewModel.onEvent(NewsCreateEvent.VoucherSelected(v.id)); showVoucherDropdown = false })
                        }
                    }
                }
            }

            OutlinedTextField(value = state.content, onValueChange = { viewModel.onEvent(NewsCreateEvent.ContentChanged(it)) }, label = { Text("Nội dung bài viết") }, modifier = Modifier.fillMaxWidth().height(200.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = state.startDateStr, onValueChange = { viewModel.onEvent(NewsCreateEvent.StartDateChanged(it)) }, label = { Text("Ngày bắt đầu") }, modifier = Modifier.weight(1f), placeholder = { Text("yyyy-mm-dd hh:mm") })
                OutlinedTextField(value = state.endDateStr, onValueChange = { viewModel.onEvent(NewsCreateEvent.EndDateChanged(it)) }, label = { Text("Ngày kết thúc") }, modifier = Modifier.weight(1f), placeholder = { Text("yyyy-mm-dd hh:mm") })
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Xuất bản ngay", fontWeight = FontWeight.Bold)
                Switch(checked = state.published, onCheckedChange = { viewModel.onEvent(NewsCreateEvent.PublishedChanged(it)) })
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}