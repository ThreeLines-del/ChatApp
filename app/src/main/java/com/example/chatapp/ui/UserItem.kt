package com.example.chatapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.User
import com.example.chatapp.ui.theme.PurpleGrey40

@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                )
                .background(Color(0xFFC6E4F1)),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFC6E4F1)
            )
        ) {

            Row(
                modifier = Modifier
                    .height(90.dp)
                    .padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.profilePic,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                user.name?.let {
                    Text(
                        text = it,
//                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        modifier = Modifier
                            .padding(15.dp),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewS(){
    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ){
        items(20){
            UserItem(user = User("John Doe", "", "", "")) {

            }
        }
    }
}