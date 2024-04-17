package com.example.redcolaboracion.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.redcolaboracion.viewmodel.EventViewModel
import kotlinx.coroutines.delay

@Composable
fun EventScreen(viewModel: EventViewModel) {
    println("Comienza view")

    LaunchedEffect(Unit) {
        viewModel.readEvent()
    }

    println("Numero registros: " + viewModel.uiEventsList.size)
    // Mostrar la lista de eventos
    LazyColumn {
        items(viewModel.uiEventsList.size) { index ->
            val event = viewModel.uiEventsList[index].value
            Text(text = event.title)
            Text(text = event.imageUrl)
            Text(text = event.content)
        }
    }
    println("Termina view")
}

@Preview(showBackground = true)
@Composable
fun PreviewEventScreen() {
    EventScreen(viewModel = EventViewModel())
}