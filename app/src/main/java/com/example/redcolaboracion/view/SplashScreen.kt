package com.example.redcolaboracion.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.navigation.AppScreens
import com.example.redcolaboracion.navigation.MainScreen
import com.example.redcolaboracion.viewmodel.EventViewModel
import com.example.redcolaboracion.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {
    LaunchedEffect(key1 = true) {
        delay(5000)
    }
    Splash()
}
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

    if (isVisible) {
        Image(
            painter = rememberAsyncImagePainter(pathImage),
            contentDescription = "Imagen de carga",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        MainScreen(eventViewModel = EventViewModel(), loginViewModel = ProfileViewModel())
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSplashScreen() {
    SplashScreen()
}
