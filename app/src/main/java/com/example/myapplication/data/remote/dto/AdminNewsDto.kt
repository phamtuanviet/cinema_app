package com.example.myapplication.data.remote.dto

data class AdminNewsDto(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val published: Boolean,
    val type: String, // "NORMAL" hoặc "VOUCHER"
    val content: String?,
    // Thời gian hiển thị bài viết
    val startDate: String?, // Định dạng: HH:mm - dd/MM/yyyy
    val endDate: String?,

    // Nếu là bài viết loại VOUCHER thì sẽ có thêm thông tin này
    val voucherCode: String?
)

// 2. Enum định nghĩa 2 Tab
enum class NewsTypeTab(val title: String, val typeValue: String) {
    NORMAL("Tin tức", "NORMAL"),
    VOUCHER("Khuyến mãi", "VOUCHER")
}

data class AdminPostUpdateRequest(
    val title: String,
    val content: String,
    val published: Boolean,
    val type: String,
    val startDate: String?, // ISO: yyyy-MM-dd'T'HH:mm:ss
    val endDate: String?,
    val voucherId: String? // ID của Voucher được chọn
)

// DTO rút gọn để hiển thị trong danh sách chọn (Dropdown)
data class AdminVoucherSimpleDto(
    val id: String,
    val code: String
)

data class AdminPostCreateRequest(
    val title: String,
    val content: String,
    val published: Boolean,
    val type: String,
    val startDate: String?, // yyyy-MM-dd'T'HH:mm:ss
    val endDate: String?,
    val voucherId: String?,
    val sendNotification: Boolean = false
)