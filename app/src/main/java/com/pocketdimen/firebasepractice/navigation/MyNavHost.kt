package com.pocketdimen.firebasepractice.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pocketdimen.firebasepractice.MyApplication
import com.pocketdimen.firebasepractice.screen.auth.SignInScreen
import com.pocketdimen.firebasepractice.screen.auth.AuthViewModel
import com.pocketdimen.firebasepractice.screen.auth.SignUpScreen
import kotlinx.serialization.Serializable

@Serializable
data object SplashScreen

@Serializable
data object HomeScreen

@Serializable
data object SignInScreen

@Serializable
data object SignUpScreen

@Serializable
data object ChatScreen

@Composable
fun MyNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(factory = MyApplication.Factory)
    val user by authViewModel.user.collectAsStateWithLifecycle()
    val uiState by authViewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = SplashScreen,
        modifier = modifier
    ) {
        composable<SplashScreen> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            LaunchedEffect(Unit) {
                if (user != null) {
                    navController.navigate(HomeScreen)
                } else {
                    navController.navigate(SignInScreen)
                }
            }
        }

        composable<HomeScreen> {
            Text(text = "Home Screen")
        }

        composable<SignUpScreen> {
            LaunchedEffect(user) {
                if (user != null) {
                    navController.navigate(HomeScreen)
                }
            }
            SignUpScreen(
                state = uiState,
                onAuthEvent = authViewModel::onEvent,
                onNavigateToSignIn = {
                    navController.navigate(SignInScreen)
                }
            )
        }
        composable<SignInScreen> {
            LaunchedEffect(user) {
                if (user != null) {
                    navController.navigate(HomeScreen)
                }
            }
            SignInScreen(
                state = uiState,
                onAuthEvent = authViewModel::onEvent,
                onNavigateToSignUp = {
                    navController.navigate(SignUpScreen)
                }
            )
        }
        composable<ChatScreen> {
            Text(text = "Chat Screen")
        }



    }
}