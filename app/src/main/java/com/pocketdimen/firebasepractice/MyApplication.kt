package com.pocketdimen.firebasepractice

import android.app.Application
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pocketdimen.firebasepractice.screen.auth.AuthViewModel
import com.pocketdimen.firebasepractice.screen.home.ChatViewModel

class MyApplication: Application() {

    companion object {
        val firebaseAuth by lazy { FirebaseAuth.getInstance() }
        val firebaseRealtime by lazy { FirebaseDatabase.getInstance() }
        val Factory = viewModelFactory {
            initializer {
                AuthViewModel(
                    auth = firebaseAuth,
                    realtime = firebaseRealtime
                )
            }
            initializer {
                ChatViewModel(
                    auth = firebaseAuth,
                    realtime = firebaseRealtime
                )
            }
        }
    }

}