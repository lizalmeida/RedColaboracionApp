package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.model.Category
import com.example.redcolaboracion.model.User
import com.example.redcolaboracion.model.UserSession
import com.example.redcolaboracion.navigation.BottomNavItem
import com.example.redcolaboracion.navigation.TopMenu
import com.example.redcolaboracion.viewmodel.CategoryViewModel
import com.example.redcolaboracion.viewmodel.ProfileViewModel
import com.google.common.collect.Iterables.addAll
import com.google.firebase.Firebase
import com.google.firebase.perf.performance
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var isLogged = UserSession.userId != null
    val scrollState = rememberScrollState()
    var profilePicUri by remember { mutableStateOf<Uri?>(null) }
    val navBackStackEntry = navController.currentBackStackEntry

    val categoryViewModel: CategoryViewModel = viewModel()
    LaunchedEffect(Unit) {
        categoryViewModel.fetchCategories()
        categoryViewModel.fetchUserCategories(UserSession.userId.toString())
    }
    val allCategories by categoryViewModel.categories.collectAsState()
    val userCategories by categoryViewModel.userCategories.collectAsState()
    val selectedCategories = remember { mutableStateListOf<Category>() }
    LaunchedEffect(userCategories) {
        if (selectedCategories.isEmpty() && userCategories.isNotEmpty()) {
            selectedCategories.addAll(userCategories)
        }
    }

    LaunchedEffect(navBackStackEntry) {
        // Intentamos recuperar el URI pasado por navegación
        val uriString = navBackStackEntry?.savedStateHandle?.get<String>("photoUri")

        // Si uriString es nulo o vacío, se procede a leer desde storage
        if (uriString.isNullOrEmpty()) {
            val userId = UserSession.userId.toString()
            viewModel.readUser(userId)
            imageUrl = imageUrl ?: viewModel.getUserProfileImageUrl(userId)
            imageUrl?.let {
                profilePicUri = Uri.parse(it)
            }
        } else {
            // Si existe un valor, lo parseamos
            profilePicUri = Uri.parse(uriString)
        }
    }
    val uiState by viewModel.uiState
    LaunchedEffect(uiState) {
        if (isLogged) {
            uiState?.let { user ->
                email = user.email
                name = user.name
                lastname = user.lastname
                imageUrl = user.imageUrl
                phone = user.phone
                address = user.address
                location = user.location
            }
        }
    }

    Scaffold(
    ) {
        TopMenu(
            title = "Perfil de Usuario",
            navController = navController
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(vertical = 40.dp)
                .verticalScroll(scrollState)
        ) {
            profilePicUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
            Button(onClick = {
                navController.navigate("camera") //CameraPreview(navController)
            }) {
                Text("Tomar Foto Principal")
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
            Row(){
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .width(220.dp)
                        .padding(4.dp),
                    label = { Text(text = "Contraseña") },
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = isLogged != true
                )
                Button(
                    onClick = {
                        // Envía la contraseña al correo
                        viewModel.sendPasswordToEmail(email)
                        Toast.makeText(context, "Contraseña enviada al correo", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f),
                    enabled = isLogged != false
                ) {
                    Text(
                        "Restablecer\nContraseña",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center)
                }
            }

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
                label = { Text(text = "Teléfono / Celular") },
                shape = RoundedCornerShape(12.dp))
            TextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Dirección") },
                shape = RoundedCornerShape(12.dp))
/*            TextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                label = { Text(text = "Ubicación") },
                shape = RoundedCornerShape(12.dp)) */

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Categorías en las que puedes ayudar:",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            CategorySelection(
                allCategories = allCategories,
                selectedCategories = selectedCategories
            )

            if (isLogged) {
                Button(
                    onClick = {
                        if (email.isNotBlank() && name.isNotBlank() && lastname.isNotBlank()) {

                            viewModel.updateUser(
                                User(email, name, lastname, imageUrl, phone, address, location),
                                selectedCategories.toSet(),
                                onSuccess = {
                                    println("Usuario: " + UserSession.userId.toString())
                                    profilePicUri?.let {
                                        viewModel.uploadImageToFirebase (
                                            photoUri = profilePicUri!!,
                                            userId = UserSession.userId.toString(),
                                            context = context,
                                            onResult = {}
                                        )
                                    }
                                    println("Datos del usuario actualizados con éxito.")
                                    Toast.makeText(
                                        context,
                                        "Datos del usuario actualizados con éxito.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    //navigationController.navigate(BottomNavItem.History.route)
                                },
                                onFailure = { exception ->
                                    println("Error al actualizar los datos del usuario: $exception")
                                    Toast.makeText(
                                        context,
                                        "Error al actualizar los datos del usuario: ${exception.message}",
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
                    Text("Actualizar Datos")
                }
            } else{
                Button(
                    onClick = {
                        val profileScreenTrace = Firebase.performance.newTrace("profile_register_time")
                        profileScreenTrace.start()

                        if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank() && lastname.isNotBlank()) {

                            viewModel.registerUser(
                                User(email, name, lastname, imageUrl, phone, address, location),
                                email,
                                password,
                                selectedCategories.toSet(),
                                onSuccess = {
                                    println("Usuario register: " + UserSession.userId.toString())
                                    profilePicUri?.let {
                                        viewModel.uploadImageToFirebase (
                                            photoUri = profilePicUri!!,
                                            userId = UserSession.userId.toString(),
                                            context = context,
                                            onResult = {}
                                        )
                                    }
                                    println("Usuario registrado con éxito.")
                                    Toast.makeText(
                                        context,
                                        "Usuario registrado con éxito.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(BottomNavItem.Home.route)
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

                            profileScreenTrace.stop()

                        } else {
                            println("Por favor, completa todos los campos.")
                            Toast.makeText(
                                context,
                                "Por favor, completa todos los campos.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 56.dp)
                ) {
                    Text("Registrar Usuario")
                }
            }
        }
    }
}

@Composable
fun CategorySelection(
    allCategories: List<Category>,
    selectedCategories: SnapshotStateList<Category>
) {
    Column {
        allCategories.forEach { category ->
            val isSelected = selectedCategories.contains(category)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSelected) {
                            selectedCategories.remove(category)
                        } else {
                            selectedCategories.add(category)
                        }
                    }
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { checked ->
                        if (checked) {
                            if (!selectedCategories.contains(category))
                                selectedCategories.add(category)
                        } else {
                            selectedCategories.remove(category)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = category.name)
            }
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