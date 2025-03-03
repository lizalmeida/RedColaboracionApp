package com.example.redcolaboracion.view

import androidx.compose.foundation.clickable
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import java.util.*

@Composable
fun DateTimePickerField(
    onDateSelected: (Timestamp) -> Unit
) {
    var fieldDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    if (showDatePicker) {

        showDatePicker = false // Ocultarlo para evitar múltiples llamadas

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        fieldDate = format.format(calendar.time)

                        onDateSelected(Timestamp(calendar.time))
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Box(
        modifier = Modifier
            .padding(10.dp)
            .clickable { showDatePicker = true }
    ) {
        TextField(
            value = fieldDate,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            label = { Text(text = "dd/MM/yyyy HH:mm") },
            enabled = false,
            onValueChange = { /* Solo lectura, no necesita implementación */ },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}
