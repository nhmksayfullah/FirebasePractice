package com.pocketdimen.firebasepractice

import android.app.Application
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.pocketdimen.firebasepractice.screen.auth.AuthViewModel

class MyApplication: Application() {

    companion object {
        val firebaseAuth by lazy { FirebaseAuth.getInstance() }
        val Factory = viewModelFactory {
            initializer {
                AuthViewModel(firebaseAuth)
            }
        }
    }

}