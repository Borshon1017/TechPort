package com.example.techport.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(onLogout: () -> Unit, viewModel: ProfileViewModel = viewModel()) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var newDisplayName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileOption(
            text = "Edit Profile",
            icon = Icons.Default.Edit,
            onClick = { showEditProfileDialog = true }
        )
        ProfileOption(
            text = "Change Password",
            icon = Icons.Default.Lock,
            onClick = { showPasswordDialog = true }
        )
        ProfileOption(
            text = "Logout",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = { showLogoutDialog = true }
        )
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                viewModel.logout()
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onConfirm = { password ->
                viewModel.changePassword(password, 
                    onSuccess = { showPasswordDialog = false }, 
                    onError = { showPasswordDialog = false })
            },
            onDismiss = { showPasswordDialog = false },
            newPassword = newPassword,
            onPasswordChange = { newPassword = it }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            onConfirm = { displayName ->
                viewModel.updateProfile(displayName, 
                    onSuccess = { showEditProfileDialog = false }, 
                    onError = { showEditProfileDialog = false })
            },
            onDismiss = { showEditProfileDialog = false },
            newDisplayName = newDisplayName,
            onDisplayNameChange = { newDisplayName = it }
        )
    }
}

@Composable
fun ProfileOption(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text)
    }
}

@Composable
fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Logout") },
        text = { Text("Are you sure you want to log out?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    newPassword: String,
    onPasswordChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            OutlinedTextField(
                value = newPassword,
                onValueChange = onPasswordChange,
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(newPassword) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditProfileDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    newDisplayName: String,
    onDisplayNameChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            OutlinedTextField(
                value = newDisplayName,
                onValueChange = onDisplayNameChange,
                label = { Text("New Display Name") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(newDisplayName) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
