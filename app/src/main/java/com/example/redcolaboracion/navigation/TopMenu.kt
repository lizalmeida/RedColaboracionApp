package com.example.redcolaboracion.navigation

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.redcolaboracion.ui.theme.Purple40
import com.example.redcolaboracion.ui.theme.Purple80

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopMenu(
    title: String,
    navController: NavController
) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        color = Purple40,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() } ) {
                        Icon(
                            //painter = painterResource(id = R.drawable.ic_arrow_back),
                            //contentDescription = stringResource(R.string.back)
                            imageVector = Icons.Filled.KeyboardArrowLeft , contentDescription = null,
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(
                            //painter = painterResource(id = R.drawable.ic_exit),
                            //contentDescription = stringResource(R.string.exit),
                            imageVector = Icons.Filled.ExitToApp , contentDescription = null,
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        content = {
            Contenido()
        }
    )
}

@Composable
fun Contenido(){
    Text(text = "Hola mundo")
}