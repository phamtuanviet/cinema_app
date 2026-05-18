package com.example.myapplication.presentation.screen.admin.movie.edit

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.presentation.screen.admin.movie.create.AgeRatingDropdown
import com.example.myapplication.presentation.screen.admin.movie.create.GenreSelectionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMovieEditScreen(
    movieId: String, // Trực tiếp nhận từ NavGraph, mặc dù ViewModel đã tự lấy
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AdminMovieEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSaveSuccess()
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onEvent(MovieEditEvent.PosterPicked(uri)) }
    )

    var showGenreDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa Phim", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                windowInsets = WindowInsets(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (!state.isLoadingData) {
                Button(
                    onClick = { viewModel.updateMovie(context) },
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
                // Lỗi khi lấy data
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else {
                // Form hiển thị y hệt Create Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Ảnh Poster (Ưu tiên ảnh mới chọn, nếu không thì lấy ảnh DB)
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

                    // ... (Tất cả các TextField, Dropdown AgeRating, Card Thể loại y hệt như màn Create)
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { viewModel.onEvent(MovieEditEvent.TitleChanged(it)) },
                        label = { Text("Tên phim (*)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = state.durationMinutes,
                            onValueChange = { viewModel.onEvent(MovieEditEvent.DurationChanged(it)) },
                            label = { Text("Thời lượng (phút)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = state.basePrice,
                            onValueChange = { viewModel.onEvent(MovieEditEvent.BasePriceChanged(it)) },
                            label = { Text("Giá vé gốc đ (*)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // 3. Ngày khởi chiếu (Tạm dùng TextField, có thể tích hợp DatePickerDialog sau)
                    OutlinedTextField(
                        value = state.releaseDate,
                        onValueChange = { viewModel.onEvent(MovieEditEvent.ReleaseDateChanged(it)) },
                        label = { Text("Ngày khởi chiếu (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 4. Dropdown Age Rating
                    AgeRatingDropdown(
                        selectedRating = state.ageRating,
                        onRatingSelected = { viewModel.onEvent(MovieEditEvent.AgeRatingChanged(it)) }
                    )



                    // 6. Mô tả & Trailer
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

    // Dialog Chọn / Thêm Thể loại (Dùng chung component GenreSelectionDialog bạn đã có)
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