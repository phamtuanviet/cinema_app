package com.example.myapplication

import com.example.myapplication.data.remote.dto.LoyaltyAccountResponse
import com.example.myapplication.data.remote.dto.LoyaltyTransactionResponse
import com.example.myapplication.data.remote.dto.VoucherDto
import com.example.myapplication.data.remote.enums.VoucherStatus
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.LoyaltyRepository
import com.example.myapplication.domain.repository.VoucherRepository
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.MediaType.Companion.toMediaType
import com.example.myapplication.presentation.screen.voucher.voucher_list.VoucherListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import io.mockk.mockk
import io.mockk.coEvery


import org.junit.Assert.*
import org.junit.Before


@OptIn(ExperimentalCoroutinesApi::class)
class VoucherListViewModelTest {

    private lateinit var voucherRepository: VoucherRepository
    private lateinit var loyaltyRepository: LoyaltyRepository
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: VoucherListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        voucherRepository = mockk()
        loyaltyRepository = mockk()

        // Định nghĩa kết quả mặc định cho mọi test (trả về Result success với dữ liệu rỗng)
        coEvery { voucherRepository.getVouchers(any()) } returns Result.success(emptyList())
        coEvery { loyaltyRepository.getLoyaltyAccount() } returns Result.success(LoyaltyAccountResponse(availablePoints = 0))
        coEvery { loyaltyRepository.getLoyaltyTransactions() } returns Result.success(emptyList())

        viewModel = VoucherListViewModel(voucherRepository, loyaltyRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addVoucher fails with friendly error message when expired`() = runTest {
        // --- CÁCH KHỞI TẠO HTTPEXCEPTION CHUẨN ---
        // Response.error cần một ResponseBody hợp lệ
        val responseBody = "expired".toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(400, responseBody)

        // HttpException constructor: (Response) hoặc (Response, cause)
        // Dùng cái nhận vào 1 đối tượng Response là đủ
        val exception = HttpException(response)

        coEvery { voucherRepository.addVoucher(any()) } returns Result.failure(exception)

        // When
        viewModel.addVoucher("EXPIRED_CODE")
        advanceUntilIdle()

        // Then
        assertEquals("Rất tiếc! Mã giảm giá này đã hết hạn sử dụng.", viewModel.state.value.error)
    }

    @Test
    fun `loadLoyalty success updates state`() = runTest {
        // Given
        val mockAccount = LoyaltyAccountResponse(availablePoints = 500)
        val mockTransactions = listOf(
            LoyaltyTransactionResponse(100, "EARN", "Xem phim", null, null, null, "2026-05-22")
        )

        coEvery { loyaltyRepository.getLoyaltyAccount() } returns Result.success(mockAccount)
        coEvery { loyaltyRepository.getLoyaltyTransactions() } returns Result.success(mockTransactions)

        // When: Switch tab sang Loyalty (index 1)
        viewModel.onMainTabChange(1)
        advanceUntilIdle()

        // Then
        assertEquals(500, viewModel.state.value.loyaltyPoint)
        assertEquals(1, viewModel.state.value.transactions.size)
        assertFalse(viewModel.state.value.isLoading)
    }
}