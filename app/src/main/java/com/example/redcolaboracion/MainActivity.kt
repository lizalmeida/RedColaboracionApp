package com.example.redcolaboracion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.redcolaboracion.ui.theme.RedColaboracionAppTheme
import com.example.redcolaboracion.view.SplashScreen
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.initialize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseApp.initializeApp(this)
        //val appCheck = FirebaseAppCheck.getInstance()
        //appCheck.installAppCheckProviderFactory(
        //    PlayIntegrityAppCheckProviderFactory.getInstance()  // O SafetyNetAppCheckProviderFactory
        //)
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            //PlayIntegrityAppCheckProviderFactory.getInstance(),
            DebugAppCheckProviderFactory.getInstance()
        )

        setContent {
            RedColaboracionAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                }
                SplashScreen()
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun Greeting() {
    Text (text = "Hello Eli")
    SplashScreen()
}
