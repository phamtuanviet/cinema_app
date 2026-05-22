package com.example.myapplication.presentation.screen.admin.combo.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminComboUpdateRequest
import com.example.myapplication.domain.repository.AdminComboRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- STATE ---
data class AdminComboEditState(
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null, // Lỗi chung cho Toast

    // Form data & Validation
    val name: String = "",
    val nameError: String? = null,

    val description: String = "",

    val priceStr: String = "",
    val priceError: String? = null,

    val isActive: Boolean = true,

    // Ảnh
    val existingImageUrl: String? = null,
    val selectedImageUri: Uri? = null
)
// --- EVENT ---
sealed class ComboEditEvent {
    data class NameChanged(val name: String) : ComboEditEvent()
    data class DescriptionChanged(val desc: String) : ComboEditEvent()
    data class PriceChanged(val price: String) : ComboEditEvent()
    data class IsActiveChanged(val isActive: Boolean) : ComboEditEvent()
    data class ImageSelected(val uri: Uri?) : ComboEditEvent()
    data class SaveClicked(val context: Context) : ComboEditEvent() // Truyền Context từ UI xuống để convert file
}

// --- VIEWMODEL ---
@HiltViewModel
class AdminComboEditViewModel @Inject constructor(
    private val repository: AdminComboRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val comboId: String = checkNotNull(savedStateHandle["comboId"])

    private val _state = MutableStateFlow(AdminComboEditState())
    val state = _state.asStateFlow()

    init {
        loadComboData()
    }

    private fun loadComboData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true, error = null) }
            val result = repository.getComboById(comboId)

            if (result.isSuccess) {
                val combo = result.getOrNull()!!
                val formattedPrice = if (combo.price % 1 == 0.0) combo.price.toLong().toString() else combo.price.toString()

                _state.update {
                    it.copy(
                        isLoadingData = false,
                        name = combo.name,
                        description = combo.description ?: "",
                        priceStr = formattedPrice,
                        isActive = combo.isActive,
                        existingImageUrl = combo.imageUrl
                    )
                }
            } else {
                _state.update { it.copy(isLoadingData = false, error = result.exceptionOrNull()?.message ?: "Lỗi tải dữ liệu") }
            }
        }
    }

    fun onEvent(event: ComboEditEvent) {
        when (event) {
            // 🔥 Tự động xóa lỗi đỏ khi bắt đầu gõ lại
            is ComboEditEvent.NameChanged -> _state.update { it.copy(name = event.name, nameError = null) }
            is ComboEditEvent.PriceChanged -> _state.update { it.copy(priceStr = event.price, priceError = null) }

            is ComboEditEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is ComboEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is ComboEditEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }
            is ComboEditEvent.SaveClicked -> saveCombo(event.context)
        }
    }

    // Hàm dọn dẹp lỗi sau khi hiện Toast
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun saveCombo(context: Context) {
        val st = _state.value
        var isValid = true

        if (st.name.isBlank()) {
            _state.update { it.copy(nameError = "Tên Combo không được để trống") }
            isValid = false
        }

        val priceDouble = st.priceStr.toDoubleOrNull()
        if (st.priceStr.isBlank() || priceDouble == null || priceDouble < 0) {
            _state.update { it.copy(priceError = "Giá bán không hợp lệ") }
            isValid = false
        }

        // Nếu có lỗi ở các ô text thì dừng lại
        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminComboUpdateRequest(
                name = st.name.trim(),
                description = st.description.trim().takeIf { it.isNotBlank() },
                price = priceDouble!!,
                isActive = st.isActive
            )

            val result = repository.updateCombo(comboId, request, st.selectedImageUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Cập nhật thất bại") }
            }
        }
    }
}