package com.example.redcolaboracion.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.model.User
import com.example.redcolaboracion.ui.theme.Purple40
import com.example.redcolaboracion.viewmodel.EventViewModel
import com.example.redcolaboracion.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (String?) -> Unit,
    onLoginFailed: (String?) -> Unit
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val profileViewModel: ProfileViewModel = viewModel()
    //var imageUrl by remember { mutableStateOf<String?>(null) }
    val imageUrl = "https://firebasestorage.googleapis.com/v0/b/redcolaboracion-7d500.appspot.com/o/images%2Flogin.jpg?alt=media&token=cd6f39d7-f68d-48f8-bd19-bcafe3d31f1e"

    Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .border(2.dp, Color.Gray, CircleShape)
                .padding(8.dp)
        ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Firebase Image",
                    modifier = Modifier.size(140.dp)
                )
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text("Email") },
            shape = RoundedCornerShape(20.dp)
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text("Password") },
            shape = RoundedCornerShape(20.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = {
                    isLoading = true

                    profileViewModel.readLogin(
                        email,
                        password,
                        onLoginSuccess = { uid ->
                            userId = uid.toString() // Asigna el UID obtenido
                            println("Inicio de sesión exitoso. UID: $uid")
                            onLoginSuccess(userId)
                        },
                        onLoginFailed = { error ->
                            errorMessage = error // Asigna el mensaje de error
                            println("Error al iniciar sesión: $error")
                            onLoginFailed(errorMessage)
                        }
                    )
                    isLoading = false
                },
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Iniciar Sesión")
            }
            // Display login error if any
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }
        }
        Text(
            text = "No tienes cuenta? Crea una",
            fontSize = 16.sp,
            color = Purple40,
            modifier = Modifier
                .clickable { navController.navigate("profile") }

        )
    }
}
