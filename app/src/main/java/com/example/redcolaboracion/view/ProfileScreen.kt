package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.redcolaboracion.model.User
import com.example.redcolaboracion.viewmodel.ProfileViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {

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
    var imageUrl by remember {
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

    Scaffold(
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Email", modifier = Modifier .size(6.dp)
                    //.padding(top = 8.dp, start = 16.dp)
            )
            TextField(
                value = email,
                onValueChange = {email = it},
                label = { Text(text = "Ingrese su email") }
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Ingrese su contraseña") })
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Ingrese su nombre") })
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text(text = "Ingrese su apellido") })
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text(text = "Tomar foto") })
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(text = "Ingrese su número de celular") })
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = "Ingrese su dirección") })
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(text = "Seleccione su ubicación") })
            Spacer(modifier = Modifier.height(4.dp))

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

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(viewModel = ProfileViewModel())
}