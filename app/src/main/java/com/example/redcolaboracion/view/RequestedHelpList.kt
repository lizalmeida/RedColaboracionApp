package com.example.redcolaboracion.view

import android.annotation.SuppressLint
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.model.RequestedHelp
import com.example.redcolaboracion.model.UserSession
import com.example.redcolaboracion.navigation.BottomNavItem
import com.example.redcolaboracion.viewmodel.RequestedHelpListViewModel
import com.google.firebase.Firebase
import com.google.firebase.perf.performance

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RequestedHelpList(viewModel: RequestedHelpListViewModel, navController: NavController) {
    val profileScreenTrace = Firebase.performance.newTrace("helps_load_time")
    profileScreenTrace.start()

    LaunchedEffect(Unit) {
        viewModel.readEvent(userId = UserSession.userId)
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row{
                Text(text = "",
                    modifier = Modifier.width(20.dp)
                )
                Text(text = "Fecha",
                    modifier = Modifier
                        .padding (horizontal = 2.dp)
                        .weight(1f)
                )
                Text(text = "Categoría",
                    modifier = Modifier
                        .width(80.dp)
                        .padding (horizontal = 2.dp)
                )
                Text(text = "Prioridad",
                    modifier = Modifier
                        .padding (horizontal = 2.dp)
                        .weight(1f)
                )
                Text(text = "Estado",
                    modifier = Modifier
                        .padding (horizontal = 2.dp)
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Divider()

            LazyColumn (
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.uiRequestedHelpList.size) { currentRequestedHelp ->
                    val requestedHelp = viewModel.uiRequestedHelpList[currentRequestedHelp]
                    RequestedHelpRow(requestedHelp){ requestedHelpId ->
                        navController.navigate("${BottomNavItem.History.route}/EfectiveHelp/$requestedHelpId")
                    }
                    Divider()
                }
            }
        }
    }
    profileScreenTrace.stop()
}

@Composable
fun RequestedHelpRow(requestedHelp: RequestedHelp, onClick:(String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(requestedHelp.id) }
    ) {
        if ( requestedHelp.efectiveHelp.toBoolean()) {
            Image(
                painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/redcolaboracion-7d500.appspot.com/o/images%2Fcheck.png?alt=media&token=35c8dc77-7bae-46df-9b0d-d01a1f1a7343"),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .padding(horizontal = 2.dp)
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/redcolaboracion-7d500.appspot.com/o/images%2Fcross.png?alt=media&token=02c50774-f140-461f-8a01-2e20968a9d62"),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .padding(horizontal = 2.dp)
            )
        }
        Text(
            text = requestedHelp.requestDate,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
        )
        Text(
            text = requestedHelp.category,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .width(80.dp)
                .padding (horizontal = 2.dp)
        )
        Text(
            text = if (requestedHelp.priority.toInt() == 1) "Urgente"
                   else if (requestedHelp.priority.toInt() == 2) "1 Día"
                   else if (requestedHelp.priority.toInt() == 3) "1 Semana" else "",
            fontSize = 12.sp,
            color = if (requestedHelp.priority.toInt() == 1) Color.Red else Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
        )
        Text(
            text = requestedHelp.status,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
        )
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewRequestedHelpList() {
    RequestedHelpList(viewModel = RequestedHelpListViewModel())
} */