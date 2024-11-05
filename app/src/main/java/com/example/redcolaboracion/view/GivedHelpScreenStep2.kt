package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redcolaboracion.model.UserSession
import com.example.redcolaboracion.navigation.BottomNavItem
import com.example.redcolaboracion.navigation.TopMenu
import com.example.redcolaboracion.viewmodel.GivedHelpViewModel
import java.time.LocalDate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GivedHelpScreenStep2(requestedHelpId: String, navController: NavController) {

    val givedHelpViewModel: GivedHelpViewModel = viewModel()
    val uiRequestedHelp by givedHelpViewModel.UIRequestedHelp

    var comments by remember {
        mutableStateOf("")
    }
    var fieldDate by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    // Ejecutar la lectura del documento cuando se cargue la pantalla
    LaunchedEffect(requestedHelpId) {
        givedHelpViewModel.readRequestedHelp(requestedHelpId)
    }
    Scaffold() {
        TopMenu(
            title = "Ofrecer Ayuda",
            navController = navController
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(vertical = 40.dp)
        ) {
            //Text(text = "ID: ${it.id}")
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
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Solicitante: ${uiRequestedHelp.requestUser}",
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
                        text = if (uiRequestedHelp.priority == "1") "Prioridad: Urgente"
                        else if (uiRequestedHelp.priority == "2") "Prioridad: 1 Día"
                        else if (uiRequestedHelp.priority == "3") "Prioridad: 1 Semana" else "",
                        color = if (uiRequestedHelp.priority == "1") Color.Red else Color.Black,
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
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Divider()

            Text(
                text = "Comentarios para el solicitante:",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
            TextField(
                value = comments,
                onValueChange = { comments = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = { Text(text = "Comentarios:") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Fecha/Hora de Entrega:",
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
                label = { Text(text = "dd/MM/yyyy") },
                shape = RoundedCornerShape(12.dp)
            )

            /*            val offeredDate = remember { mutableStateOf(LocalDate.now())}
        CustomDatePicker(
            value = offeredDate.value,
            onValueChange = {offeredDate.value = it}
        ) */
            /*            DateTextField(context = context, onDateSelected = { fecha ->
            fieldDate = fecha  // Actualizar el estado con la fecha seleccionada
        }) */
            /* TextField(
            value = offeredDate,
            onValueChange = {offeredDate = it},
            label = { Text(text = "Fecha/Hora de Entrega") }
        )*/
            val offeredDate = stringToDate(fieldDate);

            Button(
                onClick = {
                    if (comments.isNotBlank() && fieldDate.isNotBlank()) {
                        offeredDate?.let {
                            givedHelpViewModel.saveGivedHelp(
                                uidRequestedHelp = requestedHelpId,
                                comments = comments,
                                offeredDate = offeredDate,
                                givedDate = offeredDate,
                                uidUser = UserSession.userId.toString(),
                                onSuccess = {
                                    println("Ayuda ofrecida guardada con éxito.")
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
            ) {
                Text("Registrar Ofrecimiento de Ayuda")
            }
        }
    }
}