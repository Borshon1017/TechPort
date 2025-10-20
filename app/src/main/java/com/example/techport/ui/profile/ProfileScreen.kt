package com.example.techport.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun ProfileScreen(onLogout: () -> Unit, viewModel: ProfileViewModel = viewModel()) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile image center
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 32.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val photoUrl = viewModel.profilePhotoUrl ?: viewModel.auth.currentUser?.photoUrl?.toString()
            if (!photoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .border(width = 2.dp, color = Color.LightGray, shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // default circle with icon, match size/clip/border for alignment
                Box(modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = Color.LightGray, shape = CircleShape)
                    .background(Color.LightGray), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.White)
                }
            }
        }

        OutlinedButton(
            onClick = { showEditProfileDialog = true },
            shape = MaterialTheme.shapes.extraLarge,
            border = BorderStroke(1.dp, Color.LightGray),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Change Profile picture", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Keep minimal debug info: uid and email only
        val currentUser = viewModel.auth.currentUser
        Text("uid: ${currentUser?.uid ?: "<none>"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text("email: ${currentUser?.email ?: "<none>"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        if (viewModel.canChangePassword) {
            ProfileOption(
                text = "Change Password",
                icon = Icons.Default.Lock,
                onClick = { showPasswordDialog = true }
            )
        }
        ProfileOption(
            text = "Logout",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = { showLogoutDialog = true }
        )
    }

    // Change Password visibility is controlled by the ViewModel (currently defaulted on)

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
            onChangeRequested = { current, newPass, onSuccess, onError ->
                viewModel.changePassword(current, newPass, onSuccess, onError)
            },
            onDismiss = { showPasswordDialog = false }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showEditProfileDialog) {
        ChangeProfilePictureDialog(
            onConfirmUrl = { url ->
                viewModel.profilePhotoUrl = url
                viewModel.updateProfilePhoto(url,
                    onSuccess = { showEditProfileDialog = false },
                    onError = { msg -> errorMessage = msg })
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }

    SnackbarHost(hostState = snackbarHostState)

    LaunchedEffect(errorMessage) {
        val msg = errorMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            errorMessage = null
        }
    }
}

@Composable
fun ChangeProfilePictureDialog(
    onConfirmUrl: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedUrl by remember { mutableStateOf("") }

    // 12 presets (4 per row). Keep fixed sizes so images don't get squeezed.
    val predefined = listOf(
        "https://images.unsplash.com/photo-1546182990-dffeafbe841d?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1545239351-1141bd82e8a6?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1517423440428-a5a00ad493e8?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1525253086316-d0c936c814f8?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1517705008121-5f36b6b8e1f8?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1476610182048-b716b8518aae?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1524975433581-7a3f5a0b33d9?w=800&q=80&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1546182990-dffeafbe841d?w=800&q=80&auto=format&fit=crop&sat=1"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose profile picture") },
        text = {
            Column(modifier = Modifier.heightIn(max = 220.dp).padding(top = 4.dp)) {
                Text("Select from presets:")
                Spacer(modifier = Modifier.height(8.dp))

                val visible = predefined.take(6)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Spacer(modifier = Modifier.width(8.dp)) }
                    items(visible) { url ->
                        Card(modifier = Modifier
                            .size(96.dp)
                            .clickable {
                                selectedUrl = url
                                onConfirmUrl(url)
                            }, shape = CircleShape) {
                            AsyncImage(model = url, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                    }
                    item { Spacer(modifier = Modifier.width(8.dp)) }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun ProfileOption(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
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
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .shadow(elevation = 8.dp, spotColor = Color.Red, shape = MaterialTheme.shapes.extraLarge),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, Color.LightGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    "Cancel",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    onChangeRequested: (currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change password") },
        text = {
            Column {
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    label = { Text("Current password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirm new password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    error = when {
                        current.isBlank() || newPass.isBlank() || confirm.isBlank() -> "All fields are required"
                        newPass != confirm -> "Passwords do not match"
                        else -> {
                            // call into ViewModel and surface errors in this dialog
                            onChangeRequested(current, newPass,
                                { onDismiss() },
                                { msg -> error = msg })
                            ""
                        }
                    }
                },
                modifier = Modifier
                    .shadow(8.dp, shape = MaterialTheme.shapes.extraLarge),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) { Text("Change") }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, Color.LightGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) { Text("Cancel") }
        }
    )
}

