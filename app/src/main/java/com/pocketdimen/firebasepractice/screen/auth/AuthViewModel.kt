package com.pocketdimen.firebasepractice.screen.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val auth: FirebaseAuth
): ViewModel() {
    private val _user = MutableStateFlow(auth.currentUser)
    val user = _user.asStateFlow()
    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun onEvent(event: AuthUiEvent) {
        when(event) {
            AuthUiEvent.SignOut -> signOut()
            is AuthUiEvent.EmailChanged -> {
                _state.update {
                    it.copy(email = event.email)
                }
            }
            is AuthUiEvent.PasswordChanged -> {
                _state.update {
                    it.copy(password = event.password)
                }
            }
            AuthUiEvent.SignIn -> signIn()
            AuthUiEvent.SignUp -> signUp()
        }
    }


    private fun signOut() {
        auth.signOut()
        _user.value = null

    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(
            state.value.email, state.value.password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                _user.value = auth.currentUser
            }
        }.addOnFailureListener {
            _user.value = null
        }
    }

    private fun signUp() {
        auth.createUserWithEmailAndPassword(
            state.value.email, state.value.password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                _user.value = auth.currentUser
                }
        }.addOnFailureListener {
            _user.value = null
        }
    }


}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isSignedIn: Boolean = false
)

sealed class AuthUiEvent {
    data class EmailChanged(val email: String): AuthUiEvent()
    data class PasswordChanged(val password: String): AuthUiEvent()
    data object SignIn: AuthUiEvent()
    data object SignUp: AuthUiEvent()
    data object SignOut: AuthUiEvent()
}