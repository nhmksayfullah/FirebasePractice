package com.pocketdimen.firebasepractice.screen.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val auth: FirebaseAuth,
    private val realtime: FirebaseDatabase
): ViewModel() {
    private val _user = MutableStateFlow(auth.currentUser)
    val user = _user.asStateFlow()
    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    init {
        user.value?.let {
            _state.update { currentState ->
                currentState.copy(
                    email = it.email ?: ""
                )
            }
            realtime.getReference(Const.Users.value)
                .child(it.uid).get().addOnSuccessListener {
                    _state.update { currentState ->
                        currentState.copy(
                            displayName = it.child(Const.DisplayName.value).value.toString()
                        )
                    }
                }
        }

    }

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
            is AuthUiEvent.DisplayNameChanged -> {
                _state.update {
                    it.copy(displayName = event.displayName)
                }
            }
        }
    }


    private fun signOut() {
        auth.signOut()
        _user.value = null

    }
    //Users
    // email, name

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
                auth.currentUser?.let {
                    realtime.getReference(Const.Users.value).child(it.uid).setValue(
                        mapOf(
                            Const.DisplayName.value to state.value.displayName,
                            Const.Email.value to state.value.email
                        )
                    )
                }

            }
        }.addOnFailureListener {
            _user.value = null
        }
    }

}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = ""
)

sealed class AuthUiEvent {
    data class EmailChanged(val email: String): AuthUiEvent()
    data class PasswordChanged(val password: String): AuthUiEvent()
    data class DisplayNameChanged(val displayName: String): AuthUiEvent()
    data object SignIn: AuthUiEvent()
    data object SignUp: AuthUiEvent()
    data object SignOut: AuthUiEvent()
}

enum class Const(
    val value: String
) {
    Users("users"), DisplayName("displayName"), Email("email"), Password("password"),
    MyFriends("myFriends"), Uid("uid")
}