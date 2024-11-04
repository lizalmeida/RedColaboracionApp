package com.example.redcolaboracion.view


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.redcolaboracion.viewmodel.HistoryViewModel
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.redcolaboracion.navigation.TopMenu

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HistoryScreen(navController: NavController) {
    var selectedOption by remember { mutableStateOf("Solicitudes") }

    Scaffold() {
        TopMenu(
            title = "Historial de Ayudas",
            navController = navController
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(vertical = 40.dp)
        ) {
            //Filtro de tipo de ayuda (requested or gived)
            Column(
                modifier =Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == "Solicitudes",
                        onClick = { selectedOption = "Solicitudes" }
                    )
                    Text(text = "Solicitudes de Ayuda")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    RadioButton(
                        selected = selectedOption == "Realizadas",
                        onClick = { selectedOption = "Realizadas" }
                    )
                    Text(text = "Ayudas Realizadas")
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Divider()
            if (selectedOption == "Solicitudes")
                RequestedHelpList(viewModel())
            else
                GivedHelpList(viewModel())
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewHistoryScreen() {
    HistoryScreen()
}*/

