package com.example.redcolaboracion.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redcolaboracion.model.Category
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {
    val firestore: FirebaseFirestore = Firebase.firestore
    val categories = MutableStateFlow<List<Category>>(emptyList())
    val userCategories = MutableStateFlow<List<Category>>(emptyList())

    val TAG = "CategoryViewModel"

    fun readCategory() {
        val db = Firebase.firestore
        var CategoryList = mutableStateListOf<Category>()

        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                val source = if (result != null && result.metadata.hasPendingWrites()) {
                    "Local"
                } else {
                    "Server"
                }

                CategoryList.clear()
                for (doc in result) {
                    val doc_Nombre = doc["name"].toString()
                    val Category1 = Category(
                        name = doc_Nombre
                    )
                    CategoryList.add(Category1)
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener las categorías: $exception")
            }
    }

    fun fetchCategories() {
        //val categories: StateFlow<List<Category>> = _categories
        viewModelScope.launch {
            firestore.collection("category")
                .get()
                .addOnSuccessListener { result ->
                    val categoryList = result.map { document ->
                        Category(document.getString("name") ?: "")
                    }
                    categories.value = categoryList  // Actualiza el StateFlow con la nueva lista
                }
                .addOnFailureListener { exception ->
                    // Manejar error si es necesario
                    println("Error al obtener categorías: $exception")
                }
        }
    }

    fun fetchUserCategories(userId: String) {
        viewModelScope.launch {
            firestore.collection("userCategory")
                .whereEqualTo("uidUser", userId)
                .get()
                .addOnSuccessListener { result ->
                    val categoryList = result.map { document ->
                        Category(document.getString("category") ?: "")
                    }
                    userCategories.value = categoryList  // Actualiza el StateFlow con la nueva lista
                }
                .addOnFailureListener { exception ->
                    // Manejar error si es necesario
                    println("Error al obtener categorías: $exception")
                }
        }
    }
}