package com.example.myapplication

import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.dto.UserDto
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.presentation.screen.auth.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import io.mockk.mockk
import io.mockk.coEvery

import org.junit.Assert.*
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    // Mock các dependencies
    private val repository = mockk<AuthRepository>()
    private val sessionManager = mockk<SessionManager>(relaxed = true)

    // Quy định luồng chạy cho Coroutines
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(repository, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with empty email should set error state`() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("123456")

        viewModel.login()

        assertEquals("Email không được để trống", viewModel.state.value.error)
    }

    @Test
    fun `login with invalid email format should set error state`() = runTest {
        viewModel.onEmailChange("invalid-email")
        viewModel.onPasswordChange("123456")

        viewModel.login()

        assertEquals("Định dạng email không hợp lệ", viewModel.state.value.error)
    }

    @Test
    fun `login success sets success state`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        coEvery { repository.login(email, password, any()) } returns true
        coEvery { sessionManager.getUser() } returns UserDto(
            id = "1",
            email = "test@example.com",
            fullName = "Pham Tuan Viet",
            phone = "0123456789",
            isVerified = true,
            role = "USER",
            avatarUrl = null,
            isBanned = false
        )

        // When
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.login()

        // Then
        advanceUntilIdle() // Chờ coroutine chạy xong
        assertTrue(viewModel.state.value.isSuccess)
        assertEquals("USER", viewModel.state.value.role)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `login failure from server returns error message`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        coEvery { repository.login(email, password, any()) } returns false

        // When
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.login()

        // Then
        advanceUntilIdle()
        assertEquals("Đăng nhập thất bại. Kiểm tra lại thông tin!", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSuccess)
    }
}