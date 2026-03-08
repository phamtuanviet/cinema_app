package com.example.myapplication.presentation.navigation.route


sealed class VoucherRoute(val route: String) {

    // danh sách voucher của user
    object VoucherList : VoucherRoute("voucher_list")

    // thêm voucher
    object AddVoucher : VoucherRoute("add_voucher")

    // lịch sử voucher
    object VoucherHistory : VoucherRoute("voucher_history")

}