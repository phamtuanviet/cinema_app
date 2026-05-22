package com.example.myapplication

import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.presentation.screen.auth.register.RegisterViewModel
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
class RegisterViewModelTest {

    private val repository = mockk<AuthRepository>()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register with empty full name should set error`() = runTest {
        viewModel.onFullNameChange("")
        viewModel.register()
        assertEquals("Họ và tên không được để trống", viewModel.state.value.error)
    }

    @Test
    fun `register with invalid phone format should set error`() = runTest {
        viewModel.onFullNameChange("Phạm Tuấn Việt")
        viewModel.onEmailChange("viet@example.com")
        viewModel.onPhoneChange("123") // Quá ngắn
        viewModel.onPasswordChange("123456")

        viewModel.register()

        assertEquals("Số điện thoại không hợp lệ (9-11 số)", viewModel.state.value.error)
    }

    @Test
    fun `register success updates state`() = runTest {
        // Given
        val fullName = "Phạm Tuấn Việt"
        val email = "viet@example.com"
        val phone = "0901234567"
        val password = "password123"

        coEvery { repository.register(email, password, fullName, phone) } returns true

        // When
        viewModel.onFullNameChange(fullName)
        viewModel.onEmailChange(email)
        viewModel.onPhoneChange(phone)
        viewModel.onPasswordChange(password)
        viewModel.register()

        // Then
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `register API failure should set error message`() = runTest {
        // Given
        val fullName = "Phạm Tuấn Việt"
        val email = "viet@example.com"
        val phone = "0901234567"
        val password = "password123"

        // Giả lập repository ném ra Exception
        coEvery {
            repository.register(email, password, fullName, phone)
        } throws Exception("Email đã tồn tại")

        // When
        viewModel.onFullNameChange(fullName)
        viewModel.onEmailChange(email)
        viewModel.onPhoneChange(phone)
        viewModel.onPasswordChange(password)
        viewModel.register()

        // Then
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isSuccess)
        val errorMessage = viewModel.state.value.error
        assertNotNull(errorMessage)
        assertTrue(errorMessage!!.contains("Email đã tồn tại"))
    }
}