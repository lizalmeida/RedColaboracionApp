package com.example.redcolaboracion.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import com.example.redcolaboracion.viewmodel.User

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

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(value = email, onValueChange = { email = it }, label = { Text(text = "email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text(text = "password") })
        TextField(value = name, onValueChange = { name = it }, label = { Text(text = "name") })
        TextField(value = lastname, onValueChange = { lastname = it }, label = { Text(text = "lastname") })

        Button(onClick = {
            viewModel.registerUser(
                User(name, lastname),
                email,
                password
            )
        }) {
            Text("Register")
        }
    }
}

@Composable
fun UserScreen(viewModel: LoginViewModel) {

    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.readUser()
    }

    Column {
        Text(text = uiState.name)
        Text(text = uiState.lastname)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    UserScreen(viewModel = LoginViewModel())
}