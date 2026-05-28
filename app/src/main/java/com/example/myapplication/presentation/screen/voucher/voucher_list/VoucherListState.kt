package com.example.myapplication.presentation.screen.voucher.voucher_list

import com.example.myapplication.data.remote.dto.LoyaltyTransactionResponse
import com.example.myapplication.data.remote.dto.UserVoucherResponse
import com.example.myapplication.data.remote.enums.VoucherStatus

data class VoucherListState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Chỉ giữ lại những gì liên quan đến Voucher
    val selectedVoucherTab: VoucherStatus = VoucherStatus.AVAILABLE,
    val vouchers: List<UserVoucherResponse> = emptyList(),
    val isAddingVoucher: Boolean = false
)