package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.redcolaboracion.viewmodel.LoginViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.redcolaboracion.viewmodel.User

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(viewModel: LoginViewModel) {

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

    Scaffold(
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "email") })
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "password") })
            TextField(value = name, onValueChange = { name = it }, label = { Text(text = "name") })
            TextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text(text = "lastname") })
            TextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text(text = "imageUrl") })
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(text = "phone") })
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = "address") })
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(text = "location") })

            Button(onClick = {
                viewModel.registerUser(
                    User(name, lastname, imageUrl, phone, address, location),
                    email,
                    password
                )
            }) {
                Text("Register")
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserScreen(viewModel: LoginViewModel) {

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
fun PreviewLoginScreen() {
    LoginScreen(viewModel = LoginViewModel())
}