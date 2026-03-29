package com.example.myapplication.presentation.screen.profile.account

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.presentation.component.CameraPreview
import com.example.myapplication.utils.createImageUri
import com.example.myapplication.utils.uriToFile

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileAccountScreen(
    viewModel: ProfileAccountViewModel = hiltViewModel(),
    onNavigateToChangePassword: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showPicker by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }

    // ===== Permission =====
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showCamera = true
        }
    }

    // ===== Gallery =====
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.setAvatar(file)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Account", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar
        state.avatarFile?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.fullName,
            onValueChange = viewModel::onFullNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Avatar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::updateProfile,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isUpdating
        ) {
            Text(if (state.isUpdating) "Updating..." else "Update Profile")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change Password")
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }

    // ===== Bottom Sheet =====
    if (showPicker) {
        ModalBottomSheet(
            onDismissRequest = { showPicker = false }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Take Photo
                Button(
                    onClick = {
                        showPicker = false

                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (granted) {
                            showCamera = true
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take Photo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gallery
                Button(
                    onClick = {
                        showPicker = false
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick from Gallery")
                }
            }
        }
    }

    // ===== Camera Screen =====
    if (showCamera) {
        CameraPreview(
            onImageCaptured = { file ->
                viewModel.setAvatar(file)
                showCamera = false
            },
            onClose = {
                showCamera = false
            }
        )
    }
}