package com.example.chatapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.ui.AuthenticationStatus
import com.example.chatapp.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInScreen(
    viewModel: MainViewModel,
    navController: NavController
){

    val authenticationStatus = viewModel.authenticationStatus
    val context = LocalContext.current

    var email by remember{
        mutableStateOf("")
    }
    var password by remember{
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            }
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            viewModel.login(email, password)
            when(authenticationStatus.value){
                AuthenticationStatus.SUCCESS -> {
                    navController.navigate(
                        "route2"
                    )
                }
                AuthenticationStatus.FAILURE -> {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                }
                null -> {
                    Log.i("MyLog", "Auth Error")
                }
            }
        }) {
            Text(text = "Log In")
        }

        Button(onClick = {
            navController.navigate(
                "SignUp_Screen"
            )
        }) {
            Text(text = "Sign Up")
        }

    }
}