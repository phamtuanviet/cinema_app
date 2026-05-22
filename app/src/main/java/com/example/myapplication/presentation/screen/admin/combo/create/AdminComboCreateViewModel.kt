package com.example.myapplication.presentation.screen.admin.combo.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminComboCreateRequest
import com.example.myapplication.domain.repository.AdminComboRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminComboCreateState(
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Dùng cho Toast chung (như quên chọn ảnh)

    val name: String = "",
    val nameError: String? = null, // Lỗi riêng ô Tên

    val description: String = "",

    val priceStr: String = "",
    val priceError: String? = null, // Lỗi riêng ô Giá

    val isActive: Boolean = true,
    val selectedImageUri: Uri? = null
)

sealed class ComboCreateEvent {
    data class NameChanged(val name: String) : ComboCreateEvent()
    data class DescriptionChanged(val desc: String) : ComboCreateEvent()
    data class PriceChanged(val price: String) : ComboCreateEvent()
    data class IsActiveChanged(val isActive: Boolean) : ComboCreateEvent()
    data class ImageSelected(val uri: Uri?) : ComboCreateEvent()
    data class SaveClicked(val context: Context) : ComboCreateEvent()
}

@HiltViewModel
class AdminComboCreateViewModel @Inject constructor(
    private val repository: AdminComboRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminComboCreateState())
    val state = _state.asStateFlow()

    fun onEvent(event: ComboCreateEvent) {
        when (event) {
            // 🔥 Tự động xóa lỗi khi người dùng gõ lại
            is ComboCreateEvent.NameChanged -> _state.update { it.copy(name = event.name, nameError = null) }
            is ComboCreateEvent.PriceChanged -> _state.update { it.copy(priceStr = event.price, priceError = null) }

            is ComboCreateEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is ComboCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is ComboCreateEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri, error = null) }
            is ComboCreateEvent.SaveClicked -> saveCombo(event.context)
        }
    }

    // 🔥 Hàm dọn dẹp lỗi sau khi Toast hiện xong
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun saveCombo(context: Context) {
        val st = _state.value
        var isValid = true

        // 1. Kiểm tra Ảnh (Bắt buộc chọn ảnh cho đẹp UI User)
        if (st.selectedImageUri == null) {
            _state.update { it.copy(error = "Vui lòng chọn ảnh minh họa cho Combo!") }
            return
        }

        // 2. Kiểm tra Tên Combo
        if (st.name.isBlank()) {
            _state.update { it.copy(nameError = "Tên Combo không được để trống") }
            isValid = false
        }

        // 3. Kiểm tra Giá bán
        val priceDouble = st.priceStr.toDoubleOrNull()
        if (st.priceStr.isBlank() || priceDouble == null || priceDouble < 0) {
            _state.update { it.copy(priceError = "Giá bán không hợp lệ") }
            isValid = false
        }

        // Ngừng lại nếu form bị lỗi
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminComboCreateRequest(
                name = st.name.trim(),
                description = st.description.trim().takeIf { it.isNotBlank() },
                price = priceDouble!!,
                isActive = st.isActive
            )

            val result = repository.createCombo(request, st.selectedImageUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Tạo Combo thất bại!") }
            }
        }
    }
}