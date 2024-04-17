package com.example.redcolaboracion.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.redcolaboracion.viewmodel.EventViewModel
import com.example.redcolaboracion.model.Event

@Composable
fun EventScreen(viewModel: EventViewModel) {
    println("Comienza view")

    LaunchedEffect(Unit) {
        viewModel.readEvent()
    }

    println("Numero registros: " + viewModel.uiEventsList.size)
    // Mostrar la lista de eventos
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        items(viewModel.uiEventsList.size) { currentEvent ->
            val event = viewModel.uiEventsList[currentEvent]
            EventRow(event)
            Divider()
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
        Text(
            text = event.imageUrl,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Text(
                text = event.title,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = event.content,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = event.date,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventScreen() {
    EventScreen(viewModel = EventViewModel())
}