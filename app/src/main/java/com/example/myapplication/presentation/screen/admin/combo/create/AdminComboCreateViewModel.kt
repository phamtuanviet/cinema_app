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

// --- STATE ---
data class AdminComboCreateState(
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    val name: String = "",
    val description: String = "",
    val priceStr: String = "",
    val isActive: Boolean = true, // Mặc định tạo mới là đang bán

    val selectedImageUri: Uri? = null // Chỉ có ảnh chọn từ máy, không có ảnh server
)

// --- EVENT ---
sealed class ComboCreateEvent {
    data class NameChanged(val name: String) : ComboCreateEvent()
    data class DescriptionChanged(val desc: String) : ComboCreateEvent()
    data class PriceChanged(val price: String) : ComboCreateEvent()
    data class IsActiveChanged(val isActive: Boolean) : ComboCreateEvent()
    data class ImageSelected(val uri: Uri?) : ComboCreateEvent()
    data class SaveClicked(val context: Context) : ComboCreateEvent()
}

// --- VIEWMODEL ---
@HiltViewModel
class AdminComboCreateViewModel @Inject constructor(
    private val repository: AdminComboRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminComboCreateState())
    val state = _state.asStateFlow()

    fun onEvent(event: ComboCreateEvent) {
        when (event) {
            is ComboCreateEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is ComboCreateEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is ComboCreateEvent.PriceChanged -> _state.update { it.copy(priceStr = event.price) }
            is ComboCreateEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is ComboCreateEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }

            is ComboCreateEvent.SaveClicked -> saveCombo(event.context)
        }
    }

    private fun saveCombo(context: Context) {
        val currentState = _state.value

        // Validate dữ liệu
        if (currentState.name.isBlank() || currentState.priceStr.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập tên và giá bán") }
            return
        }

        val priceDouble = currentState.priceStr.toDoubleOrNull()
        if (priceDouble == null || priceDouble < 0) {
            _state.update { it.copy(error = "Giá bán không hợp lệ") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val request = AdminComboCreateRequest(
                name = currentState.name,
                description = currentState.description.takeIf { it.isNotBlank() },
                price = priceDouble,
                isActive = currentState.isActive
            )

            // Gọi Repository
            val result = repository.createCombo(request, currentState.selectedImageUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}