package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.model.Category
import com.example.redcolaboracion.model.RequestedHelp
import com.example.redcolaboracion.model.UserSession
import com.example.redcolaboracion.navigation.BottomNavItem
import com.example.redcolaboracion.navigation.TopMenu
import com.example.redcolaboracion.viewmodel.CategoryViewModel
import com.example.redcolaboracion.viewmodel.GivedHelpViewModel
import com.example.redcolaboracion.viewmodel.RequestedHelpListViewModel
import com.example.redcolaboracion.viewmodel.RequestedHelpViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GivedHelpScreen(viewModel: RequestedHelpListViewModel, navController: NavController) {

    val categoryViewModel: CategoryViewModel = viewModel()
    LaunchedEffect(Unit) {
        categoryViewModel.fetchUserCategories(UserSession.userId.toString())
    }
    val userCategories by categoryViewModel.userCategories.collectAsState()
    //val userCategories by categoryViewModel.userCategories.collectAsState(emptyList())

    val requestedHelps = viewModel.uiRequestedHelpList
    val category = remember { mutableStateOf("") }
    LaunchedEffect(category.value) {
        //viewModel.readEvent(category = "Víveres") //CORREGIR: categorias configuradas
        if (category.value.isNotEmpty()) {
            viewModel.readEvent(category = category.value)
        }
    }
    var selectedCategory by remember { mutableStateOf("") }

    Scaffold() {
        TopMenu(
            title = "Requerimientos de Ayudas",
            navController = navController
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(vertical = 40.dp)
        ) {
            userCategories.forEach { currentCategory ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedCategory == currentCategory.name,
                        onClick = {
                            selectedCategory = currentCategory.name
                            category.value = currentCategory.name
                        }
                    )
                    //Text(text = userCategories.contains(category.name))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = currentCategory.name)
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(requestedHelps.size) { currentRequestedHelp ->
                    val requestedHelp = requestedHelps[currentRequestedHelp]
                    RequestedHelpRowG(requestedHelp) { requestedHelpId ->
                        navController.navigate("${BottomNavItem.GivedHelp.route}/$requestedHelpId")
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun RequestedHelpRowG(requestedHelp: RequestedHelp, onClick:(String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(requestedHelp.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row() {
                Text(
                    text = "Id:",
                    modifier = Modifier
                        .padding(2.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = requestedHelp.id,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                )
            }
            Row() {
                Text(
                    text = "Categoría:",
                    modifier = Modifier
                        .padding(2.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = requestedHelp.category,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                )
            }
            Row() {
                Text(
                    text = "Solicitante:",
                    modifier = Modifier
                        .padding(2.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = requestedHelp.requestUser,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                )
            }
            Row() {
                Text(
                    text = "Fecha:",
                    modifier = Modifier
                        .padding(2.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = requestedHelp.requestDate,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                )
            }
            Row() {
                Text(
                    text = "Prioridad:",
                    modifier = Modifier
                        .padding(2.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (requestedHelp.priority.toInt() == 1) "Urgente"
                    else if (requestedHelp.priority.toInt() == 2) "1 Día"
                    else if (requestedHelp.priority.toInt() == 3) "1 Semana" else "",
                    fontSize = 14.sp,
                    color = if (requestedHelp.priority.toInt() == 1) Color.Red else Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                )
            }
        }
/*        Column(
            modifier = Modifier
            .weight(1f)
        ){
            Text(text = "RESPONDER",
                modifier = Modifier
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        } */
    }
    Divider()
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewGivedHelpScreen() {
    GivedHelpScreen(viewModel = RequestedHelpListViewModel())
} */