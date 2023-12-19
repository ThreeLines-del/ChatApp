package com.example.chatapp.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.chatapp.ui.MainViewModel
import com.example.chatapp.ui.UserItem
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavController
){
    val userList = viewModel.userList.collectAsState()

    // Create a rememberUpdatedState to track the dropdown state
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chats",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                        AsyncImage(
                            model = viewModel.currentUserProfilePic.value,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        expanded = true
                                    }
                                ),
                            contentScale = ContentScale.Crop
                        )

                    // Create a DropdownMenu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // Add menu items
                        Text(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        viewModel.logOut()
                                        navController.navigate(
                                            "route1"
                                        )
                                    }
                                ),
                            text = "Log Out"
                        )
                    }
                }
            )
        }
    ) {
        val items = userList.value
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ){
            items(items.size){ index ->
                val user = items[index]

                if (user != null) {
                    UserItem(
                        user = user,
                        onClick = {
                            val encodedUri = URLEncoder.encode(user.profilePic, StandardCharsets.UTF_8.toString())
                            navController.navigate(
                                "Chat_Screen/${user.name}/${user.uid}/${encodedUri}"
                            )
                            user.profilePic?.let { it1 -> Log.i("Sent Image", it1) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
            }
        }

    }
}