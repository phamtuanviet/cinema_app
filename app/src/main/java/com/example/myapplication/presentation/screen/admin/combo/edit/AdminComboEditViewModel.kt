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
    val error: String? = null,

    val name: String = "",
    val description: String = "",
    val priceStr: String = "",
    val isActive: Boolean = true,

    // Ảnh hiện tại từ server (URL)
    val existingImageUrl: String? = null,
    // Ảnh mới được chọn từ thư viện máy (URI)
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

                // Format giá tiền hiển thị ra UI bỏ số thập phân nếu là số nguyên (vd: 50000.0 -> 50000)
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
                _state.update { it.copy(isLoadingData = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun onEvent(event: ComboEditEvent) {
        when (event) {
            is ComboEditEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is ComboEditEvent.DescriptionChanged -> _state.update { it.copy(description = event.desc) }
            is ComboEditEvent.PriceChanged -> _state.update { it.copy(priceStr = event.price) }
            is ComboEditEvent.IsActiveChanged -> _state.update { it.copy(isActive = event.isActive) }
            is ComboEditEvent.ImageSelected -> _state.update { it.copy(selectedImageUri = event.uri) }

            is ComboEditEvent.SaveClicked -> saveCombo(event.context)
        }
    }

    private fun saveCombo(context: Context) {
        val currentState = _state.value
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

            val request = AdminComboUpdateRequest(
                name = currentState.name,
                description = currentState.description.takeIf { it.isNotBlank() },
                price = priceDouble,
                isActive = currentState.isActive
            )

            val result = repository.updateCombo(comboId, request, currentState.selectedImageUri, context)

            if (result.isSuccess) {
                _state.update { it.copy(isSaving = false, isSuccess = true) }
            } else {
                _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}