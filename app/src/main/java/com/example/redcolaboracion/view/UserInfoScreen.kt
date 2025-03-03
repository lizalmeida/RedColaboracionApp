package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.navigation.TopMenu
import com.example.redcolaboracion.viewmodel.ProfileViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserInfoScreen(
    userId: String,
    navController: NavController
) {
    val profileViewModel: ProfileViewModel = viewModel()
    val uiState by profileViewModel.uiState
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        profileViewModel.readUser(userId)
        imageUrl = profileViewModel.getUserProfileImageUrl(userId)
    }

    Scaffold(
        topBar = {
            TopMenu(
                title = "Información del Usuario",
                navController = navController
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(vertical = 40.dp)
        ) {
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Captured Image",
                    modifier = Modifier.size(140.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Email: ${uiState.email}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Nombres: ${uiState.name}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Apellidos: ${uiState.lastname}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Teléfono / Celular: ${uiState.phone}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dirección: ${uiState.address}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(viewModel = ProfileViewModel())
}*/