package com.example.myapplication.presentation.screen.admin.movie.edit

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.presentation.screen.admin.movie.create.AgeRatingDropdown
import com.example.myapplication.presentation.screen.admin.movie.create.GenreSelectionDialog
import android.app.DatePickerDialog
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import java.util.Calendar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMovieEditScreen(
    movieId: String,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminMovieEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // 🔥 Xử lý chuyển trang & Toast
    LaunchedEffect(state.isSuccess, state.error) {
        if (state.isSuccess) {
            Toast.makeText(context, "Cập nhật phim thành công!", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
        state.error?.let {
            // Bỏ qua lỗi Loading lúc mới vào màn hình nếu title đang rỗng
            if (!state.isLoadingData) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onEvent(MovieEditEvent.PosterPicked(uri)) }
    )

    var showGenreDialog by remember { mutableStateOf(false) }

    // 🔥 LỊCH DATEPICKER
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            viewModel.onEvent(MovieEditEvent.ReleaseDateChanged(formattedDate))
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
                        text = "Chỉnh sửa phim",
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
            if (!state.isLoadingData && state.error == null) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.updateMovie(context)
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    else Text("Cập nhật Phim", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.isLoadingData) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.title.isEmpty()) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Ảnh Poster
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                            .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        contentAlignment = Alignment.Center
                    ) {
                        val imageToShow = state.newPosterUri ?: state.currentPosterUrl

                        if (imageToShow != null) {
                            AsyncImage(
                                model = imageToShow,
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

                    // 2. Thông tin cơ bản có bắt lỗi
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { viewModel.onEvent(MovieEditEvent.TitleChanged(it)) },
                        label = { Text("Tên phim (*)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = state.titleError != null,
                        supportingText = { state.titleError?.let { Text(it) } }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = state.durationMinutes,
                            onValueChange = { viewModel.onEvent(MovieEditEvent.DurationChanged(it)) },
                            label = { Text("Thời lượng (phút)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = state.durationError != null,
                            supportingText = { state.durationError?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = state.basePrice,
                            onValueChange = { viewModel.onEvent(MovieEditEvent.BasePriceChanged(it)) },
                            label = { Text("Giá vé gốc đ (*)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = state.priceError != null,
                            supportingText = { state.priceError?.let { Text(it) } }
                        )
                    }

                    // 3. Ngày khởi chiếu (Bật Lịch)
                    OutlinedTextField(
                        value = state.releaseDate,
                        onValueChange = {}, // Chặn gõ tay
                        readOnly = true,
                        label = { Text("Ngày khởi chiếu (*)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focusManager.clearFocus()
                                datePickerDialog.show()
                            },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = if (state.releaseDateError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            disabledLabelColor = if (state.releaseDateError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Chọn ngày") },
                        isError = state.releaseDateError != null,
                        supportingText = { state.releaseDateError?.let { Text(it) } }
                    )

                    // 4. Age Rating (Giữ nguyên)
                    AgeRatingDropdown(
                        selectedRating = state.ageRating,
                        onRatingSelected = { viewModel.onEvent(MovieEditEvent.AgeRatingChanged(it)) }
                    )

                    // 5. Mô tả & Trailer (Giữ nguyên)
                    OutlinedTextField(
                        value = state.trailerUrl,
                        onValueChange = { viewModel.onEvent(MovieEditEvent.TrailerUrlChanged(it)) },
                        label = { Text("Link Trailer (YouTube URL)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.onEvent(MovieEditEvent.DescriptionChanged(it)) },
                        label = { Text("Mô tả nội dung phim") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5
                    )

                    // 6. Thể loại (Giữ nguyên)
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

                    // 7. Cài đặt trạng thái
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = state.isActive,
                            onCheckedChange = { viewModel.onEvent(MovieEditEvent.IsActiveChanged(it)) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (state.isActive) "Đang hoạt động (Hiển thị cho User)" else "Đã ẩn")
                    }
                }
            }
        }
    }

    // Dialog (Giữ nguyên dùng chung)
    if (showGenreDialog) {
        GenreSelectionDialog(
            availableGenres = state.availableGenres,
            selectedGenres = state.selectedGenres,
            newGenres = state.newGenres,
            onToggleGenre = { viewModel.onEvent(MovieEditEvent.GenreToggled(it)) },
            onAddNewGenre = { viewModel.onEvent(MovieEditEvent.NewGenreAdded(it)) },
            onRemoveNewGenre = { viewModel.onEvent(MovieEditEvent.NewGenreRemoved(it)) },
            onDismiss = { showGenreDialog = false }
        )
    }
}