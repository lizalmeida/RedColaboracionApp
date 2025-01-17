package com.example.redcolaboracion.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
//import com.thisisthetime.customdatepicker.ui.theme.CustomdatepickerTheme
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit
) {

    val open = remember { mutableStateOf(false)}

    if (open.value) {
        CalendarDialog(
            state = rememberUseCaseState(visible = true, true, onCloseRequest = { open.value = false } ),
            config = CalendarConfig(
                yearSelection = true,
                style = CalendarStyle.MONTH,
            ),
            selection = CalendarSelection.Date(
                selectedDate = value
            ) { newDate ->
                onValueChange(newDate)
            },
        )
    }

    TextField(
        modifier = Modifier.clickable { //Click event
            open.value = true
        },
        enabled = false,// <- Add this to make click event work
        value = value.format(DateTimeFormatter.ISO_DATE) ,
        onValueChange = {},
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant)
    )
}

fun stringToDate(dateString: String, pattern: String = "dd/MM/yyyy"): Date? {
    val formatter = SimpleDateFormat(pattern)
    return try {
        formatter.parse(dateString)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@Composable
fun DateTimePickerField(
    label: String = "dd/MM/yyyy HH:mm",
    onDateSelected: (Timestamp) -> Unit
) {
    var fieldDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // DatePickerDialog para seleccionar la fecha
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)

            // TimePickerDialog para seleccionar la hora
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)

                    // Formatear la fecha y hora seleccionada
                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    fieldDate = format.format(calendar.time)

                    // Convertir a Timestamp y devolverlo
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

    // Componente de texto con clic para abrir el calendario
    Box(
        modifier = Modifier
            .padding(10.dp)
            .clickable { datePickerDialog } // Mostrar el selector al hacer clic
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            enabled = true,
            value = fieldDate,
            onValueChange = { /* Solo lectura, no necesita implementación */ },
            label = { Text(text = label) },
            shape = RoundedCornerShape(12.dp),
            //readOnly = true, // Hace que el campo sea de solo lectura
            singleLine = true
        )
    }
}
