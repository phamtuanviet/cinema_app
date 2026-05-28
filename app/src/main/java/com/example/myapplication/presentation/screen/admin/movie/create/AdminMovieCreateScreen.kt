package com.example.myapplication.presentation.screen.admin.movie.create

import java.util.Calendar
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.AdminGenreDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMovieCreateScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminMovieCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.error, state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Thêm phim thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }


    // Launcher mở thư viện ảnh
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onEvent(MovieCreateEvent.PosterPicked(uri)) }
    )

    // State quản lý Dialog Thể loại
    var showGenreDialog by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Định dạng chuẩn xác YYYY-MM-DD
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            viewModel.onEvent(MovieCreateEvent.ReleaseDateChanged(formattedDate))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thêm phim mới",
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
        },
        bottomBar = {
            Button(
                onClick = { viewModel.createMovie(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Lưu Phim", style = MaterialTheme.typography.titleMedium)
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
            // 1. Ảnh Poster
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.posterUri != null) {
                    AsyncImage(
                        model = state.posterUri,
                        contentDescription = "Poster",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Text("Nhấn để chọn ảnh Poster", color = Color.Gray)
                    }
                }
            }

            // 2. Thông tin cơ bản
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(MovieCreateEvent.TitleChanged(it)) },
                label = { Text("Tên phim (*)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.titleError != null,
                supportingText = { state.titleError?.let { Text(it) } }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.durationMinutes,
                    onValueChange = { viewModel.onEvent(MovieCreateEvent.DurationChanged(it)) },
                    label = { Text("Thời lượng (phút)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = state.durationError != null,
                    supportingText = { state.durationError?.let { Text(it) } }
                )
                OutlinedTextField(
                    value = state.basePrice,
                    onValueChange = { viewModel.onEvent(MovieCreateEvent.BasePriceChanged(it)) },
                    label = { Text("Giá vé gốc đ (*)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = state.priceError != null,
                    supportingText = { state.priceError?.let { Text(it) } }
                )
            }

            // 3. Ngày khởi chiếu (Tạm dùng TextField, có thể tích hợp DatePickerDialog sau)
            OutlinedTextField(
                value = state.releaseDate,
                onValueChange = {}, // Không cho gõ tay
                readOnly = true,    // Khóa bàn phím
                label = { Text("Ngày khởi chiếu (*)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        focusManager.clearFocus() // Ẩn bàn phím nếu đang mở
                        datePickerDialog.show()   // Hiển thị lịch
                    },
                enabled = false, // Vô hiệu hóa để bắt sự kiện click ở TextFieldWrapper
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (state.releaseDateError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    disabledLabelColor = if (state.releaseDateError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Chọn ngày") },
                isError = state.releaseDateError != null,
                supportingText = { state.releaseDateError?.let { Text(it) } }
            )

            // 4. Dropdown Age Rating
            AgeRatingDropdown(
                selectedRating = state.ageRating,
                onRatingSelected = { viewModel.onEvent(MovieCreateEvent.AgeRatingChanged(it)) }
            )

            // 5. Thể loại (Bấm để mở Dialog)
            OutlinedCard(
                onClick = { showGenreDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Thể loại", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    val selectedText = (state.selectedGenres.map { it.name } + state.newGenres).joinToString(", ")
                    if (selectedText.isNotEmpty()) {
                        Text(selectedText, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Chưa chọn thể loại nào", color = Color.Gray)
                    }
                }
            }

            // 6. Mô tả & Trailer
            OutlinedTextField(
                value = state.trailerUrl,
                onValueChange = { viewModel.onEvent(MovieCreateEvent.TrailerUrlChanged(it)) },
                label = { Text("Link Trailer (YouTube URL)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(MovieCreateEvent.DescriptionChanged(it)) },
                label = { Text("Mô tả nội dung phim") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            // 7. Trạng thái hoạt động
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = "Cài đặt bổ sung",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Công tắc: Đang hoạt động
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (state.isActive) "Phim đang mở (Hiển thị cho User)" else "Đang ẩn phim",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Switch(
                            checked = state.isActive,
                            onCheckedChange = { viewModel.onEvent(MovieCreateEvent.IsActiveChanged(it)) }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // 🔥 Công tắc mới: Gửi Thông báo Push
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gửi thông báo (Push Notification)",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Báo cho tất cả người dùng biết có phim mới.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = state.sendNotification,
                            onCheckedChange = { viewModel.onEvent(MovieCreateEvent.SendNotificationChanged(it)) }
                        )
                    }
                }
            }
        }
    }

    // Dialog Chọn / Thêm Thể loại
    if (showGenreDialog) {
        GenreSelectionDialog(
            availableGenres = state.availableGenres,
            selectedGenres = state.selectedGenres,
            newGenres = state.newGenres,
            onToggleGenre = { viewModel.onEvent(MovieCreateEvent.GenreToggled(it)) },
            onAddNewGenre = { viewModel.onEvent(MovieCreateEvent.NewGenreAdded(it)) },
            onRemoveNewGenre = { viewModel.onEvent(MovieCreateEvent.NewGenreRemoved(it)) },
            onDismiss = { showGenreDialog = false }
        )
    }
}

// ================= CÁC COMPONENT PHỤ (Dropdown & Dialog) =================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeRatingDropdown(selectedRating: String, onRatingSelected: (String) -> Unit) {
    // List chuẩn cho chuẩn rạp Việt Nam hoặc Quốc tế (Theo yêu cầu của bạn)
    val ratings = listOf("P", "PG-13", "R", "NC-17", "K", "T13", "T16", "T18", "C")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedRating,
            onValueChange = {},
            readOnly = true,
            label = { Text("Giới hạn độ tuổi") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ratings.forEach { rating ->
                DropdownMenuItem(
                    text = { Text(rating) },
                    onClick = {
                        onRatingSelected(rating)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GenreSelectionDialog(
    availableGenres: List<AdminGenreDto>,
    selectedGenres: List<AdminGenreDto>,
    newGenres: List<String>,
    onToggleGenre: (AdminGenreDto) -> Unit,
    onAddNewGenre: (String) -> Unit,
    onRemoveNewGenre: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newGenreText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Chọn Thể loại", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Nhập thể loại mới
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newGenreText,
                        onValueChange = { newGenreText = it },
                        placeholder = { Text("Nhập thể loại khác...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newGenreText.isNotBlank()) {
                                onAddNewGenre(newGenreText)
                                newGenreText = ""
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // Danh sách
                LazyColumn(modifier = Modifier.weight(1f)) {
                    // Hiển thị các thể loại mới vừa gõ
                    items(newGenres) { name ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { onRemoveNewGenre(name) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    // Hiển thị các thể loại từ Database
                    items(availableGenres) { genre ->
                        val isSelected = selectedGenres.contains(genre)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleGenre(genre) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = isSelected, onCheckedChange = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(genre.name)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Xong")
                }
            }
        }
    }
}