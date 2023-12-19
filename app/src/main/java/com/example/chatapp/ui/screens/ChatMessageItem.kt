package com.example.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatapp.Message
import java.util.Date

@Composable
fun ChatMessageItem(message: Message, currentUserID: String) {
    val isSentMessage = message.senderId == currentUserID

    val messageBackgroundColor = if (isSentMessage) {
        Color(0xFF2196F3) // Example color for sent messages
    } else {
        Color(0xFF329728) // Example color for received messages
    }

    val textColor = if (isSentMessage) {
        Color.Black
    } else {
        Color(0xFF2196F3)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(messageBackgroundColor)
        ) {
            message.message?.let {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    color = textColor,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun Preview(){
    ChatMessageItem(message = Message("Hello", "", Date()), currentUserID = "")
}