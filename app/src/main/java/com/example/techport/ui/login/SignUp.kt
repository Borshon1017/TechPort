package com.example.techport.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techport.ui.theme.TechPOrtTheme

@Composable
fun SignUpScreen(
    onSignUp: (
        firstName: String,
        lastName: String,
        nickname: String,
        email: String,
        password: String
    ) -> Unit = { _, _, _, _, _ -> },
    onAlreadyHaveAccount: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") } // optional
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Create Account", style = MaterialTheme.typography.headlineLarge)
            Text("Please fill in the details", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(24.dp))

            // First + Last name
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Nickname (optional)
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Confirm password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    error = when {
                        firstName.isBlank() || lastName.isBlank() ||
                                email.isBlank() || password.isBlank() || confirm.isBlank() ->
                            "All required fields must be filled"
                        password != confirm -> "Passwords do not match"
                        else -> {
                            onSignUp(firstName, lastName, nickname, email, password)
                            ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Sign up") }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onForgotPassword() }
                    .padding(top = 4.dp)
            )

            Spacer(Modifier.height(12.dp))

            val annotated = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                ) { append("Already have an account? ") }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                ) { append("Log in here") }
            }

            Text(
                text = annotated,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onAlreadyHaveAccount() }
                    .padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    TechPOrtTheme { SignUpScreen() }
}
