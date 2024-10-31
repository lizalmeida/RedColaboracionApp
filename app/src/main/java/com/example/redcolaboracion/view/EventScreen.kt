package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.viewmodel.EventViewModel
import com.example.redcolaboracion.model.Event
import com.example.redcolaboracion.ui.theme.Purple80

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventScreen(viewModel: EventViewModel) {
    val pathImage = "https://firebasestorage.googleapis.com/v0/b/redcolaboracion-7d500.appspot.com/o/images%2Fdireccion_cell.png?alt=media&token=7bdf1212-26ff-4617-8a81-eac41deef8d5"

    LaunchedEffect(Unit) {
        viewModel.readEvent()
    }

    Scaffold() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                ){
                    Image(
                        painter = rememberAsyncImagePainter(pathImage),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(350.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            // Mostrar la lista de eventos
            LazyColumn (
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.uiEventsList.size) { currentEvent ->
                    val event = viewModel.uiEventsList[currentEvent]
                    EventRow(event)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun EventRow(event: Event) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(event.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Column (
            modifier = Modifier
                .padding(4.dp)
        ){
            Text(
                text = event.title,
                fontSize = 16.sp,
                color = Purple80,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
            )
            Text(
                text = event.content,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
            )
            Row{
                Text(
                    text = "Próxima reunión: ",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                )
                Text(
                    text = event.date,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventScreen() {
    EventScreen(viewModel = EventViewModel())
}