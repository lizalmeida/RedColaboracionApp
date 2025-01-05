package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redcolaboracion.navigation.BottomNavItem
import com.example.redcolaboracion.navigation.TopMenu
import com.example.redcolaboracion.viewmodel.RequestedHelpViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EfectiveHelpScreen(requestedHelpId: String, navController: NavController) {
    val requestedHelpViewModel: RequestedHelpViewModel = viewModel()
    val uiRequestedHelp by requestedHelpViewModel.UIRequestedHelp

    var selectedOption by remember { mutableStateOf("SI") }

    var fieldDate by remember {
        mutableStateOf("")
    }
    var efectiveHelp by remember {
        mutableStateOf(true)
    }
    var efectiveComments by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    // Ejecutar la lectura del documento cuando se cargue la pantalla
    LaunchedEffect(requestedHelpId) {
        requestedHelpViewModel.readRequestedHelp(requestedHelpId)
    }

    Scaffold(
        topBar = {
            TopMenu(
                title = "Cierre de Solicitud de Ayuda",
                navController = navController
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Set the size of the Box
                    .border(
                        width = 2.dp,                // Border thickness
                        color = Color.Blue,           // Border color
                        shape = RoundedCornerShape(16.dp) // Rounded corners with 16.dp radius
                    )
                    .padding(16.dp) // Optional padding inside the Box
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth() // Fill the Box space
                ) {
                    Text(
                        text = "Categoría: ${uiRequestedHelp.category}",
                        //text = "Categoría: Víveres",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Fecha Solicitud: ${uiRequestedHelp.requestDate}",
                        //text = "Fecha Solicitud: 08/11/2024",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Detalle: \$${uiRequestedHelp.requestMessage}",
                        //text = "Detalle: No tengo trabajo desde hace 3 meses, ayúdenme con comida para mi familia.",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Persona que le ayudó: Elizabeth Almeida",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Fecha de ofrecimiento de la ayuda: 08/11/2024",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Divider()

            Column(
                modifier =Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == "SI",
                        onClick = { selectedOption = "SI" }
                    )
                    Text(text = "SI recibí la ayuda")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    RadioButton(
                        selected = selectedOption == "NO",
                        onClick = { selectedOption = "NO" }
                    )
                    Text(text = "NO recibí la ayuda")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Fecha/Hora de Ayuda:",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
            TextField(
                value = fieldDate,
                onValueChange = { fieldDate = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = { Text(text = "dd/MM/yyyy HH:mm") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Comentarios sobre la ayuda recibida:",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
            TextField(
                value = efectiveComments,
                onValueChange = { efectiveComments = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = { Text(text = "Comentarios:") },
                shape = RoundedCornerShape(12.dp)
            )

            val efectiveDate = stringToDate(fieldDate);

            Button(
                onClick = {
                    if (efectiveComments.isNotBlank() && fieldDate.isNotBlank()) {
                        efectiveDate?.let {
                            requestedHelpViewModel.endedRequestHelp(
                                uidRequestedHelp = requestedHelpId,
                                efectiveDate = efectiveDate,
                                efectiveHelp = efectiveHelp,
                                efectiveComments = efectiveComments,
                                onSuccess = {
                                    println("Ayuda finalizada con éxito.")
                                    Toast.makeText(
                                        context,
                                        "Ayuda ofrecida guardada con éxito.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(BottomNavItem.History.route)
                                }
                            ) { exception ->
                                println("Error al enviar ayuda ofrecida: $exception")
                                Toast.makeText(
                                    context,
                                    "Error al enviar ayuda ofrecida: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )  {
                Text("Cerrar Solicitud de Ayuda")
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewEfectiveHelpScreen() {
    efectiveHelpScreen()
} */