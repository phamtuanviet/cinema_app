package com.example.myapplication.presentation.screen.voucher.voucher_list

import com.example.myapplication.data.remote.dto.LoyaltyTransactionResponse
import com.example.myapplication.data.remote.dto.UserVoucherResponse
import com.example.myapplication.data.remote.enums.VoucherStatus

data class VoucherListState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // tab chính
    val selectedMainTab: Int = 0,

    // tab voucher
    val selectedVoucherTab: VoucherStatus = VoucherStatus.AVAILABLE,
    // data voucher
    val vouchers: List<UserVoucherResponse> = emptyList(),

    // data loyalty
    val loyaltyPoint: Int = 0,
    val transactions: List<LoyaltyTransactionResponse> = emptyList(),

    // add voucher
    val isAddingVoucher: Boolean = false
)