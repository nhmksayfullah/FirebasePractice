package com.pocketdimen.firebasepractice.firestore

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun UploadProductScreen(
    client: Client,
    firestore: FirebaseFirestore,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var productName by remember {
        mutableStateOf("")
    }
    var productDescription by remember {
        mutableStateOf("")
    }
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = productName,
            onValueChange = {
                productName = it
            },
            label = { Text("Product Name") }
        )
        TextField(
            value = productDescription,
            onValueChange = {
                productDescription = it
            },
            label = { Text("Product Description") }
        )

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Select Image")
        }
        // Display selected image URI
        selectedImageUri?.let {
            Text(text = "Selected Image: $it")
        }

        Button(
            onClick = {
                if (productName.isNotEmpty() && productDescription.isNotEmpty() && selectedImageUri != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        uploadProduct(
                            productName = productName,
                            productDescription = productDescription,
                            imageUri = selectedImageUri!!,
                            client = client,
                            firestore = firestore,
                            context = context
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Upload Product")
        }
    }


}


suspend fun uploadProduct(
    productName: String,
    productDescription: String,
    imageUri: Uri,
    client: Client,
    firestore: FirebaseFirestore,
    context: Context
) {


        try {
            // Convert Uri to File
            val imageFile = uriToFile(imageUri, context)

            if (imageFile != null) {
                val inputFile = InputFile.fromFile(imageFile)

                val storage = Storage(client)

                // Upload the image to Appwrite Storage
                val result = storage.createFile(
                    bucketId = "678a9c95002991f90071",
                    fileId = ID.unique(), // Automatically generate a unique ID
                    file = inputFile
                )
                // Save product data to Firestore
                val productData = hashMapOf(
                    "name" to productName,
                    "description" to productDescription,
                    "imageId" to result.id
                )

                firestore.collection("products")
                    .add(productData)
                    .addOnSuccessListener {
                        Log.d("UploadProduct", "Product uploaded successfully!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("UploadProduct", "Failed to upload product: ${e.message}")
                    }
            } else {
                Log.e("UploadProduct", "Failed to create file from Uri.")
            }
        } catch (e: AppwriteException) {
            Log.e("UploadProduct", "AppwriteException: ${e.message}")
        } catch (e: Exception) {
            Log.e("UploadProduct", "Exception: ${e.message}")
        }

}

// Helper function to convert Uri to File
private fun uriToFile(uri: Uri, context: Context): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        Log.e("UploadProduct", "Failed to convert Uri to File: ${e.message}")
        null
    }
}