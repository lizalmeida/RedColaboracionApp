package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redcolaboracion.model.Category
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {
    private val TAG = "CategoryViewModel"
    private val firestore: FirebaseFirestore = Firebase.firestore
    val categories = MutableStateFlow<List<Category>>(emptyList())
    val userCategories = MutableStateFlow<List<Category>>(emptyList())

    fun fetchCategories() {
        viewModelScope.launch {
            firestore.collection("category")
                .get()
                .addOnSuccessListener { result ->
                    val categoryList = result.map { document ->
                        Category(document.getString("name") ?: "")
                    }
                    categories.value = categoryList
                    Log.i(TAG, "Categorías recuperadas con éxito.")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener categorías: $exception")
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
                    userCategories.value = categoryList
                    Log.i(TAG, "Categorías por usuario recuperadas con éxito.")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener categorías por usuario: $exception")
                }
        }
    }
}