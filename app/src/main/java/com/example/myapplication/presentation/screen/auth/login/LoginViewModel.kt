package com.example.myapplication.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password, error = null)
    }

    fun login() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        // 1. Kiểm tra Validate Email & Mật khẩu (Giữ nguyên đoạn code cũ của bạn...)
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (email.isEmpty()) {
            _state.value = _state.value.copy(error = "Email không được để trống")
            return
        } else if (!email.matches(emailRegex)) {
            _state.value = _state.value.copy(error = "Định dạng email không hợp lệ")
            return
        }
        if (password.isEmpty()) {
            _state.value = _state.value.copy(error = "Mật khẩu không được để trống")
            return
        } else if (password.length < 6) {
            _state.value = _state.value.copy(error = "Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        // 2. Dữ liệu hợp lệ -> Gọi API
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result = repository.login(
                    email = email,
                    password = password,
                    sessionManager.getFcmToken()
                )

                if (result) {
                    val user = sessionManager.getUser()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true,
                        role = user?.role
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Đăng nhập thất bại. Kiểm tra lại thông tin!"
                    )
                }

            } catch (e: HttpException) {
                // 🔥 ĐÂY LÀ NƠI BẮT LỖI TỪ SERVER TRẢ VỀ (403, 500, v.v.)
                val errorBody = e.response()?.errorBody()?.string()

                val serverMessage = try {
                    // Server Spring Boot mặc định trả về JSON có trường "message"
                    // Chúng ta dùng JSONObject có sẵn của Android để trích xuất nó ra
                    JSONObject(errorBody ?: "").getString("message")
                } catch (jsonException: Exception) {
                    // Phòng trường hợp JSON trả về không đúng cấu trúc
                    "Đăng nhập thất bại. Vui lòng thử lại!"
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = serverMessage // Gán đúng tin nhắn của Server vào đây để Snackbar hiển thị
                )

            } catch (e: IOException) {
                // Lỗi mất mạng, không kết nối được đến Server
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Không có kết nối internet. Vui lòng thử lại!"
                )
            } catch (e: Exception) {
                // Các lỗi phát sinh khác
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Đăng nhập thất bại!"
                )
            }
        }
    }
}