package com.example.myapplication

import com.example.myapplication.core.datastore.SessionManager
import com.example.myapplication.data.remote.dto.UserDto
import com.example.myapplication.domain.repository.UserRepository
import com.example.myapplication.presentation.screen.profile.account.ProfileAccountViewModel
import com.google.common.base.Verify.verify
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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow

import org.junit.Assert.*
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileAccountViewModelTest {

    private val userRepository = mockk<UserRepository>()
    private val sessionManager = mockk<SessionManager>(relaxed = true)

    // Giả lập luồng dữ liệu người dùng
    private val userFlow = MutableSharedFlow<UserDto?>()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ProfileAccountViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock hành vi của sessionManager.userFlow
        every { sessionManager.userFlow } returns userFlow

        viewModel = ProfileAccountViewModel(userRepository, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initially loads user from session manager`() = runTest {
        val testUser = UserDto(id = "1", fullName = "Pham Tuan Viet", email = "test@ex.com", phone = "090", isVerified = true, role = "USER", isBanned = false)

        // Phát dữ liệu vào luồng
        userFlow.emit(testUser)
        advanceUntilIdle()

        assertEquals("Pham Tuan Viet", viewModel.state.value.fullName)
        assertEquals(testUser, viewModel.state.value.user)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `updateProfile fails when name is blank`() = runTest {
        viewModel.onFullNameChange("   ")
        viewModel.updateProfile()

        assertEquals("Họ và tên không được để trống!", viewModel.state.value.error)
    }

    @Test
    fun `updateProfile stops if no changes detected`() = runTest {
        val user = UserDto(id = "1", fullName = "Viet", email = "v@ex.com", phone = "090", isVerified = true, role = "USER", isBanned = false)
        userFlow.emit(user)
        advanceUntilIdle()

        viewModel.onFullNameChange("Viet") // Tên giống cũ
        viewModel.updateProfile()

        assertEquals("Bạn chưa thay đổi thông tin nào.", viewModel.state.value.error)
    }

    @Test
    fun `updateProfile success updates session and state`() = runTest {
        // Given
        val oldUser = UserDto(id = "1", fullName = "Viet", email = "v@ex.com", phone = "090", isVerified = true, role = "USER", isBanned = false)
        val newUser = oldUser.copy(fullName = "Viet New")
        userFlow.emit(oldUser)
        advanceUntilIdle()

        viewModel.onFullNameChange("Viet New")
        coEvery { userRepository.updateProfile(any(), any()) } returns Result.success(newUser)

        // When
        viewModel.updateProfile()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.updateSuccess)
        assertEquals("Viet New", viewModel.state.value.user?.fullName)
        coVerify(exactly = 1) { sessionManager.saveUser(newUser) }    }
}