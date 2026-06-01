package com.example.myapplication.presentation.screen.booking.checkout



import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.MainActivity
import com.example.myapplication.data.remote.enums.PaymentMethod
import com.example.myapplication.utils.openPayment
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingCheckoutScreen(
    bookingId: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit,
    onNavigateBack : () -> Unit,
    viewModel: BookingCheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val appViewModel = (LocalContext.current as MainActivity).appViewModel
    val paymentResult by appViewModel.paymentResult.collectAsState()

    LaunchedEffect(paymentResult) {
        Log.d("CHECK_PAYMENT", "paymentResult = $paymentResult")
    }

    LaunchedEffect(state.paymentUrl) {
        state.paymentUrl?.let { url ->
            openPayment(context, url)
            viewModel.clearPaymentUrl()
        }
    }

    // =========================
    // 🔥 HANDLE RETURN
    // =========================
    LaunchedEffect(paymentResult) {
        Log.d("CHECK_PAYMENT", "paymentResult2 = $paymentResult")

        val result = paymentResult ?: return@LaunchedEffect

        if (result.code == "00") {
            onPaymentSuccess()
        } else {
            onPaymentFailed()
        }

        appViewModel.clearPaymentResult()
    }

    // =========================
    // UI
    // =========================
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            // Nền Background và bọc Column để đồng bộ với các màn trước
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Thanh toán",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        // Gọi hàm onNavigateBack để quay lại
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
                // Đường kẻ mờ dưới TopBar
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            }
        },
        bottomBar = {
            // Đưa nút thanh toán ghim ở dưới cùng màn hình
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.createPayment(bookingId) },
                        enabled = state.selectedPaymentMethod != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp), // Đồng bộ chiều cao nút
                        shape = RoundedCornerShape(16.dp) // Đồng bộ độ bo góc nút
                    ) {
                        Text(
                            text = "Thanh toán ngay",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Phương thức thanh toán",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Thẻ chọn VNPAY =====
            val isVnpaySelected = state.selectedPaymentMethod == PaymentMethod.VNPAY

            Surface(
                onClick = { viewModel.selectPaymentMethod(PaymentMethod.VNPAY) },
                shape = RoundedCornerShape(12.dp),
                // Đổi màu nền nếu được chọn
                color = if (isVnpaySelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
                // Viền đậm hơn nếu được chọn
                border = BorderStroke(
                    width = if (isVnpaySelected) 2.dp else 1.dp,
                    color = if (isVnpaySelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.vnpay),
                        contentDescription = "Logo VNPAY",
                        modifier = Modifier
                            .width(50.dp)
                            .height(36.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Tên phương thức
                    Text(
                        text = "Ví điện tử VNPAY",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    // Trạng thái chọn (Icon check)
                    if (isVnpaySelected) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Đã chọn",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        // Vòng tròn xám trống khi chưa chọn
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}