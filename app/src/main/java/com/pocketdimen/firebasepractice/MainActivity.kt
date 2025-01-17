package com.pocketdimen.firebasepractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pocketdimen.firebasepractice.firestore.ShowProductsScreen
import com.pocketdimen.firebasepractice.firestore.UploadProductScreen
import com.pocketdimen.firebasepractice.navigation.MyNavHost
import com.pocketdimen.firebasepractice.ui.theme.FirebasePracticeTheme
import io.appwrite.Client

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = Client(this).setProject("678a93a00002e120de9d")
        val firebaseFirestore = FirebaseFirestore.getInstance()
        enableEdgeToEdge()
        setContent {
            FirebasePracticeTheme {
                Scaffold {
//                    UploadProductScreen(
//                        client = client,
//                        firestore = firebaseFirestore,
//                        modifier = Modifier.padding(it)
//                    )
                    ShowProductsScreen(
                        client =  client,
                        firestore = firebaseFirestore,
                        modifier = Modifier.padding(it)
                    )
                }
            }
        }
    }
}

