package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redcolaboracion.navigation.BottomNavItem
import com.example.redcolaboracion.navigation.TopMenu
import com.example.redcolaboracion.viewmodel.RequestedHelpViewModel
import com.google.firebase.Timestamp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EfectiveHelpScreen(requestedHelpId: String, navController: NavController) {
    val TAG = "EfectiveHelpScreen"
    val requestedHelpViewModel: RequestedHelpViewModel = viewModel()
    val uiRequestedHelp by requestedHelpViewModel.UIRequestedHelp

    var selectedOption by remember { mutableStateOf("SI") }

    var efectiveDate by remember {
        mutableStateOf<Timestamp?>(null)
    }
    var efectiveHelp by remember {
        mutableStateOf(true)
    }
    var efectiveComments by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

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
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Color.Blue,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Categoría: ${uiRequestedHelp.category}",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Fecha Solicitud: ${uiRequestedHelp.requestDate}",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Detalle: \$${uiRequestedHelp.requestMessage}",
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
                        onClick = {
                            selectedOption = "SI"
                            efectiveHelp = true
                        }
                    )
                    Text(text = "SI recibí la ayuda")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    RadioButton(
                        selected = selectedOption == "NO",
                        onClick = {
                            selectedOption = "NO"
                            efectiveHelp = false
                        }
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
            DateTimePickerField(
                onDateSelected = { timestamp ->
                    efectiveDate = timestamp
                    println("Timestamp seleccionado: $timestamp")
                }
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

            Button(
                onClick = {
                    if (efectiveComments.isNotBlank()) {
                        efectiveDate?.let {
                            requestedHelpViewModel.endedRequestHelp(
                                uidRequestedHelp = requestedHelpId,
                                efectiveDate = efectiveDate,
                                efectiveHelp = efectiveHelp,
                                efectiveComments = efectiveComments,
                                onSuccess = {
                                    Log.i(TAG, "Ayuda ofrecida guardada con éxito.")
                                    Toast.makeText(
                                        context,
                                        "Ayuda ofrecida guardada con éxito.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(BottomNavItem.History.route)
                                }
                            ) { exception ->
                                Log.e(TAG, "Error al enviar ayuda ofrecida. ", exception)
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