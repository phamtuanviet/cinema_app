package com.example.myapplication.presentation.screen.booking.other_options

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.component.ComboSection
import com.example.myapplication.presentation.component.MovieHeaderOption
import com.example.myapplication.presentation.component.PointsSection
import com.example.myapplication.presentation.component.PriceSummary
import com.example.myapplication.presentation.component.VoucherInputSection
import com.example.myapplication.presentation.component.VoucherSection
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingOtherOptionsScreen(
    seatHoldSessionId: String,
    onSuccessResult: (bookingId: String) -> Unit,
    onNavigateBack: () -> Unit, // 🔥 Thêm tham số này để xử lý nút Back
    viewModel: BookingOtherOptionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(seatHoldSessionId) {
        viewModel.loadData(seatHoldSessionId)
    }

    Scaffold(
        modifier = Modifier.imePadding(), // 🔥 Quan trọng: Tự động đẩy nội dung lên khi bật bàn phím
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Dịch vụ & Thanh toán", // Tiêu đề phù hợp cho màn hình này
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
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 🔥 Loading
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // ❌ Error
                state.error != null -> {
                    Text(
                        text = state.error!!,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // ✅ Content
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        focusManager.clearFocus() // Tắt keyboard khi click ra ngoài
                                    }
                                )
                            }
                    ) {
                        // 🎬 Movie info
                        MovieHeaderOption(
                            movie = state.movie,
                            showtime = state.showtime
                        )

                        // 🍿 Combo
                        ComboSection(
                            combos = state.combos,
                            selectedCombos = state.selectedCombos,
                            onIncrease = viewModel::increaseCombo,
                            onDecrease = viewModel::decreaseCombo
                        )

                        Spacer(Modifier.height(16.dp))

                        // 🎟 Voucher input
                        VoucherInputSection(
                            voucherInput = state.voucherInput,
                            onInputChange = viewModel::onVoucherInputChange,
                            onAddClick = { viewModel.addVoucher() }
                        )

                        Spacer(Modifier.height(16.dp))

                        // 🎟 Voucher list
                        VoucherSection(
                            vouchers = state.vouchers,
                            selectedVoucherId = state.selectedVoucherId,
                            currentSubtotal = state.subtotal,
                            onSelect = viewModel::selectVoucher
                        )

                        Spacer(Modifier.height(16.dp))

                        // 💰 Points
                        PointsSection(
                            availablePoints = state.availablePoints,
                            usedPoints = state.usedPoints,
                            onChange = viewModel::changeUsedPoints
                        )

                        Spacer(Modifier.height(16.dp))

                        // 💵 Price
                        PriceSummary(state)

                        Spacer(Modifier.height(24.dp))

                        // 💳 Button
                        Button(
                            onClick = {
                                viewModel.createBooking(seatHoldSessionId, onSuccessResult)
                            },
                            enabled = state.totalAmount > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp), // Nút to và bo góc chuẩn MD3
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Thanh toán: ${state.totalAmount.toInt()}đ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}