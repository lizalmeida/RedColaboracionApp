package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.model.User
import com.example.redcolaboracion.navigation.TopMenu
import com.example.redcolaboracion.viewmodel.ProfileViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var lastname by remember {
        mutableStateOf("")
    }
    var phone by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }
    var location by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    // Llamador de la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            // Convertimos la imagen a Uri temporal o procesamos el bitmap
            imageUri = Uri.parse(bitmap.toString())
        }
    }

    // Cargar la imagen desde Firebase cuando el Composable se inicie
    LaunchedEffect(Unit) {
        imageUrl = getImageFromFirebase("images/foto.jpg")
    }

    Scaffold(
    ) {
        TopMenu(
            title = "Historial de Ayudas",
            navController = navController
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(vertical = 40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(150.dp)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable { navController.navigate("camera") } // Llamada a la cámara al hacer clic
                    .padding(8.dp)
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Captured Image",
                        modifier = Modifier.size(140.dp)
                    )
                } else if (imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Firebase Image",
                        modifier = Modifier.size(140.dp)
                    )
                } else {
                    Text("Agregar Foto", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            TextField(
                value = email,
                onValueChange = {email = it},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Email@") },
                shape = RoundedCornerShape(12.dp)
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Contraseña") },
                shape = RoundedCornerShape(12.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Nombres") },
                shape = RoundedCornerShape(12.dp))
            TextField(
                value = lastname,
                onValueChange = { lastname = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Apellidos") },
                shape = RoundedCornerShape(12.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Ingrese su número de celular") },
                shape = RoundedCornerShape(12.dp))
            TextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Ingrese su dirección") },
                shape = RoundedCornerShape(12.dp))
            TextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Seleccione su ubicación") },
                shape = RoundedCornerShape(12.dp))

            Button(onClick = {
                if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank() && lastname.isNotBlank()) {
                    viewModel.registerUser(
                        User(name, lastname, imageUrl, phone, address, location),
                        email,
                        password,
                        onSuccess = {
                            println("Usuario registrado con éxito.")
                            Toast.makeText(
                                context,
                                "Usuario registrado con éxito.",
                                Toast.LENGTH_SHORT
                            ).show()
                            //navigationController.navigate(BottomNavItem.History.route)
                        },
                        onFailure = { exception ->
                            println("Error al registrar el usuario: $exception")
                            Toast.makeText(
                                context,
                                "Error al registrar el usuario: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                } else {
                    println("Por favor, completa todos los campos.")
                    Toast.makeText(
                        context,
                        "Por favor, completa todos los campos.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Usuario")
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserScreen(viewModel: ProfileViewModel) {

    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.readUser()
    }
    Scaffold(

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = uiState.name)
            Text(text = uiState.lastname)
            Text(text = uiState.imageUrl)
            Text(text = uiState.phone)
            Text(text = uiState.address)
            Text(text = uiState.location)
        }
    }
}

suspend fun getImageFromFirebase(path: String): String? {
    return try {
        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        storageRef.downloadUrl.await().toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(viewModel = ProfileViewModel())
}*/