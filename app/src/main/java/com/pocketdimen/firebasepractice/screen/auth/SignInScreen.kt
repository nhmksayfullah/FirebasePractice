package com.pocketdimen.firebasepractice.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SignInScreen(
    state: AuthUiState,
    modifier: Modifier = Modifier,
    onAuthEvent: (AuthUiEvent) -> Unit = {},
    onNavigateToSignUp: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                onAuthEvent(AuthUiEvent.EmailChanged(it))
            },
            label = {
                Text(text = "Email")
            }
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                onAuthEvent(AuthUiEvent.PasswordChanged(it))
            },
            label = {
                Text(text = "Email")
            }
        )
        Button(
            onClick = {
                onAuthEvent(AuthUiEvent.SignIn)
            }
        ) {
            Text("Sign In")
        }

        Row {
            Text("Don't have an account?")
            TextButton(
                onClick = onNavigateToSignUp
            ) {
                Text("Sign Up")
            }
        }
    }
}