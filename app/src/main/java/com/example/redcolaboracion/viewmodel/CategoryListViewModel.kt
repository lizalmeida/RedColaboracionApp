package com.example.redcolaboracion.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.example.redcolaboracion.model.Category
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CategoryListViewModel {
    var CategoryList = mutableStateListOf<Category>()
    val TAG = "CategoryListViewModel"

    fun readCategory() {
        val db = Firebase.firestore

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
}