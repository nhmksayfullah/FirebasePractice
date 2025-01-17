package com.pocketdimen.firebasepractice.firestore

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream

@Composable
fun ImagePickerButton(
    onImageReady: (MultipartBody.Part) -> Unit
) {
    val context = LocalContext.current

    // State to store the selected image URI
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for the gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

    // Process the selected image for upload
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val imageBytes = inputStream?.readBytes()
                inputStream?.close()

                if (imageBytes != null) {
                    val filePart = MultipartBody.Part.createFormData(
                        "file",
                        "image.jpg",
                        RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
                    )
                    onImageReady(filePart)
                } else {
                    Log.e("ImagePickerButton", "Failed to read image bytes.")
                }
            } catch (e: Exception) {
                Log.e("ImagePickerButton", "Error processing image: ${e.message}")
            }
        }
    }

    // Button to open the gallery
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text(text = "Pick an Image")
        }
    }
}
