package com.example.myapplication.presentation.screen.admin.user.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse
import com.example.myapplication.data.remote.dto.AdminUserDto
import com.example.myapplication.data.remote.dto.AdminUserUpdateRequest
import com.example.myapplication.data.remote.dto.UserRoleTab
import com.example.myapplication.domain.repository.AdminUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUserListState(
    val users: List<AdminUserDto> = emptyList(),
    val isLoadingFirstPage: Boolean = true,
    val isPaginating: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val currentTab: UserRoleTab = UserRoleTab.ADMIN, // Mặc định mở tab Admin trước hoặc User tùy bạn

    val currentPage: Int = 0,
    val isLastPage: Boolean = false,

    val selectedUserForEdit: AdminUserDto? = null,
    val isUpdatingUser: Boolean = false,
    val showConfirmDialog: Boolean = false,

    // Lưu nháp các thay đổi trên Bottom Sheet trước khi lưu
    val draftRole: String = "",
    val draftIsBanned: Boolean = false,
    val draftIsVerified: Boolean = false
)

@HiltViewModel
class AdminUserListViewModel @Inject constructor(
    private val repository: AdminUserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUserListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
//        loadFirstPage()
    }

    fun prepareForReturn() {
        _state.update {
            it.copy(
                users = emptyList(),
                isLoadingFirstPage = true,
                error = null
            )
        }
    }


    fun onSearchQueryChange(query: String) {
        if (_state.value.searchQuery == query) return

        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500)
            loadFirstPage()
        }
    }

    fun openEditSheet(user: AdminUserDto) {
        _state.update {
            it.copy(
                selectedUserForEdit = user,
                draftRole = user.role,
                draftIsBanned = user.isBanned,
                draftIsVerified = user.isVerified
            )
        }
    }

    fun closeEditSheet() {
        _state.update { it.copy(selectedUserForEdit = null) }
    }

    fun updateDraftState(role: String? = null, isBanned: Boolean? = null, isVerified: Boolean? = null) {
        _state.update {
            it.copy(
                draftRole = role ?: it.draftRole,
                draftIsBanned = isBanned ?: it.draftIsBanned,
                draftIsVerified = isVerified ?: it.draftIsVerified
            )
        }
    }

    fun onTabSelected(tab: UserRoleTab) {
        if (_state.value.currentTab == tab) return
        _state.update { it.copy(currentTab = tab) }
        loadFirstPage()
    }

    fun onSaveClicked() {
        val st = _state.value
        val user = st.selectedUserForEdit ?: return

        // Nếu Role hoặc Ban bị thay đổi -> Bật Dialog cảnh báo
        if (st.draftRole != user.role || st.draftIsBanned != user.isBanned) {
            _state.update { it.copy(showConfirmDialog = true) }
        } else {
            // Chỉ đổi Verified -> Update luôn không cần hỏi
            executeUpdate()
        }
    }

    fun cancelConfirmDialog() {
        _state.update { it.copy(showConfirmDialog = false) }
    }

    fun confirmUpdate() {
        _state.update { it.copy(showConfirmDialog = false) }
        executeUpdate()
    }

    private fun executeUpdate() {
        val st = _state.value
        val user = st.selectedUserForEdit ?: return

        viewModelScope.launch {
            _state.update { it.copy(isUpdatingUser = true) }
            val request = AdminUserUpdateRequest(st.draftRole, st.draftIsBanned, st.draftIsVerified)

            val result = repository.updateUser(user.id, request)

            if (result.isSuccess) {
                val updatedUser = result.getOrNull()!!
                // Thay thế user cũ bằng user mới trong List trực tiếp, không cần gọi lại API list
                val updatedList = st.users.map { if (it.id == updatedUser.id) updatedUser else it }

                _state.update {
                    it.copy(
                        isUpdatingUser = false,
                        users = updatedList,
                        selectedUserForEdit = null // Đóng sheet
                    )
                }
            } else {
                _state.update { it.copy(isUpdatingUser = false, error = "Lỗi cập nhật: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoadingFirstPage = true, error = null, currentPage = 0, isLastPage = false, users = emptyList())
            }

            val currentState = _state.value
            val result = repository.getUsers(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                role = currentState.currentTab.roleFilter,
                page = 0
            )

            handleResult(result, isFirstPage = true)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState.isLoadingFirstPage || currentState.isPaginating || currentState.isLastPage) return

        viewModelScope.launch {
            _state.update { it.copy(isPaginating = true, error = null) }

            val nextPage = currentState.currentPage + 1
            val result = repository.getUsers(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                role = currentState.currentTab.roleFilter,
                page = nextPage
            )

            handleResult(result, isFirstPage = false, nextPage = nextPage)
        }
    }

    private fun handleResult(result: Result<AdminPaginatedResponse<AdminUserDto>>, isFirstPage: Boolean, nextPage: Int = 0) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            _state.update { currentState ->
                val newItems = if (isFirstPage) response?.content.orEmpty() else currentState.users + response?.content.orEmpty()
                currentState.copy(
                    isLoadingFirstPage = false,
                    isPaginating = false,
                    users = newItems,
                    currentPage = nextPage,
                    isLastPage = response?.isLast ?: true
                )
            }
        } else {
            _state.update {
                it.copy(isLoadingFirstPage = false, isPaginating = false, error = result.exceptionOrNull()?.message)
            }
        }
    }
}