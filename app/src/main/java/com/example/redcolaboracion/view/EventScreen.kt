package com.example.redcolaboracion.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.redcolaboracion.viewmodel.EventViewModel

@Composable
fun EventScreen(viewModel: EventViewModel) {

    val uiEvent by viewModel.uiEvent

    LaunchedEffect(Unit) {
        viewModel.readEvent()
    }

    Column {
        Text(text = uiEvent.title)
        Text(text = uiEvent.imageUrl)
        Text(text = uiEvent.content)
        Text(text = uiEvent.date)
        Text(text = uiEvent.startPublishDate)
        Text(text = uiEvent.endPublishDate)    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventScreen() {
    EventScreen(viewModel = EventViewModel())
}