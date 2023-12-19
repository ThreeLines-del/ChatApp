package com.example.chatapp.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.ui.MainViewModel

@Composable
fun ImageUploadScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        imageUri = uri
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the selected image
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to launch image picker
        Button(onClick = {
            // Launch the photo picker and let the user choose only images.
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text("Pick Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to upload the selected image
        Button(
            onClick = {
                imageUri?.let {
//                    viewModel.profilePic.value = it
                }
                navController.navigate(
                    "LogIn_Screen"
                )
            },
            enabled = imageUri != null
        ) {
            Text("Continue")
        }

        // Observe upload result
        val uploadResult = viewModel.uploadResult.value
        if (uploadResult is MainViewModel.UploadResult.Success) {
            // Handle success, for example, display the uploaded image URL
            Text("Image Uploaded: ${uploadResult.imageUrl}")
        } else if (uploadResult is MainViewModel.UploadResult.Failure) {
            // Handle failure
            Text("Image Upload Failed")
        }
    }
}