package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redcolaboracion.model.Category
import com.example.redcolaboracion.model.LoginUIState
import com.example.redcolaboracion.model.RequestedHelp
import com.example.redcolaboracion.model.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.http.Tag
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class RequestedHelpViewModel: ViewModel() {
    val firestore: FirebaseFirestore = Firebase.firestore
    //var categories = mutableStateListOf<Category>()
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories
    val TAG = "RequestedHelpViewModel"
    var UIRequestedHelp = mutableStateOf(RequestedHelp())

    fun saveRequestHelp(
        requestMessage: String,
        requestDate: Date,
        category: String,
        priority: Int,
        status: String,
        efectiveHelp: Boolean,
        //efectiveDate: Date?,  // Nullable Date
        efectiveDate: Date,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val data = mapOf(
                "requestMessage" to requestMessage,
                "requestDate" to Timestamp(requestDate),
                "category" to category,
                "priority" to priority,
                "status" to status,
                "efectiveHelp" to efectiveHelp,
                "efectiveDate" to Timestamp(efectiveDate),
                "userId" to userId
            )

            firestore.collection("requestedHelp")
                .add(data)
                .addOnSuccessListener {
                    println("Solicitud de ayuda guardada con éxito.")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    println("Error al guardar la solicitud: $exception")
                    onFailure(exception)
                }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            firestore.collection("category")
                .get()
                .addOnSuccessListener { result ->
                    val categoryList = result.map { document ->
                        Category(document.getString("name") ?: "")
                    }
                    _categories.value = categoryList  // Actualiza el StateFlow con la nueva lista
                }
                .addOnFailureListener { exception ->
                    // Manejar error si es necesario
                    println("Error al obtener categorías: $exception")
                }
        }
    }
}