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
import androidx.compose.runtime.setValue
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
import com.google.firebase.Firebase
import com.google.firebase.perf.performance
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

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
    val temporaryImageUri = remember { mutableStateOf<Uri?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var imageFile by remember { mutableStateOf<String?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLogged = UserSession.userId != null
    val userId = UserSession.userId.toString()
    val photoUriState = remember { mutableStateOf<Uri?>(null) }
    val scrollState = rememberScrollState()

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

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            success ->
        if (!success) photoUri = null
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            println("PhotoCapture" + "Foto capturada correctamente: ") // ${photoUriState.value}")
        } else {
            println("PhotoCapture" + "Error al capturar la foto")
        }
    }

    fun createTempImageFile(context: Context): Uri {
        val tempFile = File.createTempFile("profile_photo", ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        return Uri.fromFile(tempFile)
    }

    val categoryViewModel: CategoryViewModel = viewModel()
    LaunchedEffect(Unit) {
        categoryViewModel.fetchCategories()
        categoryViewModel.fetchUserCategories(UserSession.userId.toString())
    }
    val allCategories by categoryViewModel.categories.collectAsState()
    val userCategories by categoryViewModel.userCategories.collectAsState()
    LaunchedEffect(allCategories, userCategories) {
        println("All Categories: $allCategories")
        println("User Categories: $userCategories")
    }
    val selectedCategoryNames = userCategories.map { it.name }.toSet()
    //val selectedCategoryNames = remember { mutableStateOf(userCategories.map { it.name }.toSet()) }
    println("Selected Category IDs: $selectedCategoryNames")

    //var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedCategories by remember { mutableStateOf(setOf<Category>()) }

    LaunchedEffect(Unit) {
        if (isLogged) {
            viewModel.readUser(UserSession.userId.toString())
            imageUrl = imageUrl ?: viewModel.getUserProfileImageUrl(UserSession.userId.toString())
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
            if (imageUrl != null || temporaryImageUri.value != null) {
                val imageUri = temporaryImageUri.value ?: imageUrl?.let { Uri.parse(it) }
                println("ImageUrl: " + imageUrl)
                println("temporaryImageUri: " + temporaryImageUri)
                println("ImageUri: " + imageUri.toString())

                Image(
                    //model = imageUri,
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            } else {
                Button(onClick = {
                    val tempUri = createTempImageFile(context)
                    photoUri = tempUri
                    photoLauncher.launch(tempUri)
                }) {
                    Text("Tomar Foto")
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
            allCategories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = selectedCategoryNames.contains(category.name), //cambiar para que permita modificar
                        onCheckedChange = { isChecked ->
                            selectedCategories = if (isChecked) {
                                println("Category Selected: ${category.name}")
                                selectedCategories + category
                            } else {
                                println("Category Deselected: ${category.name}")
                                selectedCategories - category
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = category.name)
                }
            }

            if (isLogged) {
                Button(
                    onClick = {
                        if (email.isNotBlank() && name.isNotBlank() && lastname.isNotBlank()) {
                                viewModel.updateUser(
                                    User(email, name, lastname, imageUrl, phone, address, location),
                                    selectedCategories,
                                    onSuccess = {
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
                            /* viewModel.renameProfileImage(
                                onSuccess = {
                                    println("Imagen renombrada y movida con éxito.")
                                },
                                onFailure = { error ->
                                    println("Error al renombrar la imagen: ${error.message}")
                                }
                            ) */
                            viewModel.registerUser(
                                User(email, name, lastname, imageUrl, phone, address, location),
                                email,
                                password,
                                selectedCategories,
                                onSuccess = {
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
                            photoUri?.let {
                                viewModel.uploadImageToFirebase (
                                    photoUri = photoUri!!,
                                    userId = userId,
                                    context = context,
                                    onResult = {}
                                )
                            }
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