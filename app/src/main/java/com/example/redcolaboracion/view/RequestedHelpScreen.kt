package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.redcolaboracion.model.Category
import com.example.redcolaboracion.viewmodel.RequestedHelpViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RequestedHelpScreen(viewModel: RequestedHelpViewModel, navController: NavController) {
    val categories by viewModel.categories.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    var requestMessage by remember {
        mutableStateOf("")
    }
    var selectedPriority by remember { mutableStateOf("1") }

    //val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    //val currentDateAndTime_sdf = sdf.format(Date())
    //val currentDateAndTime = LocalDate.parse(currentDateAndTime_sdf, formatter)

    val currentDateAndTime = Date()
    //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    //val efectiveDateL = LocalDate.parse("2030-01-01 12:00:00", formatter)

    val defaultZoneId = ZoneId.systemDefault();
    val localDate = LocalDate.of(2030, 1, 1);
    val efectiveDate = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

    val context = LocalContext.current

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Text(
                text = "Escoge la categoría de ayuda que necesitas:",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
            categories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = category.name)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Detalle de su situación:",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
            TextField(
                value = requestMessage,
                onValueChange = {requestMessage = it},
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Prioridad:",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(20.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                RadioButton(
                    selected = selectedPriority == "1",
                    onClick = { selectedPriority = "1" }
                )
                Text(text = "Urgente")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPriority == "2",
                    onClick = { selectedPriority = "2" }
                )
                Text(text = "1 día")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPriority == "3",
                    onClick = { selectedPriority = "3" }
                )
                Text(text = "1 semana")
            }
            // Botón de envío
            Button(
                onClick = {
                    if (selectedCategory != null && requestMessage.isNotBlank()) {
                        viewModel.saveRequestHelp(
                            requestMessage = requestMessage,
                            requestDate = currentDateAndTime,
                            category = selectedCategory!!.name,
                            priority = selectedPriority.toInt(),
                            status = "Solicitada",
                            efectiveHelp = false,
                            efectiveDate = efectiveDate,
                            userId = "wlNqrN2IZCaQRAaryjQ5dSrx76H2",
                            onSuccess = {
                                println("Solicitud de ayuda guardada con éxito.")
                                Toast.makeText(
                                    context,
                                    "Solicitud de ayuda guardada con éxito.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //navigationController.navigate(BottomNavItem.History.route)
                            },
                            onFailure = { exception ->
                                println("Error al enviar solicitud de ayuda: $exception")
                                Toast.makeText(
                                    context,
                                    "Error al guardar la solicitud de ayuda: ${exception.message}",
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
                Text("Enviar Solicitud de Ayuda")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRequestedHelpScreen() {
    //RequestedHelpScreen(viewModel = RequestedHelpViewModel())
}