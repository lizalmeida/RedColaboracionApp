package com.example.redcolaboracion.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.navigation.MainScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen() {
    LaunchedEffect(key1 = true) {
        delay(5000)
    }
    Splash()
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Splash(){
    var isVisible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val pathImage = "https://firebasestorage.googleapis.com/v0/b/redcolaboracion-7d500.appspot.com/o/images%2Fievi_pantalla_inicial.jpg?alt=media&token=42c78dda-0008-41bf-9446-970a6c99e6e6"

    LaunchedEffect(true) {
        coroutineScope.launch {
            delay(5000)
            isVisible = false
        }
    }

    val auth = FirebaseAuth.getInstance()
    LaunchedEffect(Unit) {
        auth.signOut()  // Forzar logout al entrar al login
    }

    if (isVisible) {
        Image(
            painter = rememberAsyncImagePainter(pathImage),
            contentDescription = "Imagen de carga",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        MainScreen()
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSplashScreen() {
    SplashScreen()
}
