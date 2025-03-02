package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp

class EventViewModel: ViewModel() {
    private val TAG = "EventViewModel"

    var uiEventsList = mutableStateListOf<Event>()

    fun readEvent() {
        val db = Firebase.firestore
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val docRef = db.collection("events").get()
        docRef.addOnSuccessListener  { documents ->
            val source = if (documents != null && documents.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            if (documents != null && !documents.isEmpty) {
                Log.d(TAG, "Eventos recuperados con éxito.")

                uiEventsList.clear()
                for (doc in documents) {
                    val docId = doc["id"].toString()
                    val docTitle = doc["title"].toString()
                    val docContent = doc["content"].toString()
                    val docImageUrl = doc["imageUrl"].toString()
                    val timestampFromFirestore: Timestamp = doc["date"] as Timestamp
                    val docDate = formato.format(timestampFromFirestore.toDate()).toString()
                    val event1 = Event(
                        id = docId,
                        title = docTitle,
                        content = docContent,
                        imageUrl = docImageUrl,
                        date = docDate,
                        startPublishDate = "",
                        endPublishDate = "")
                    uiEventsList.add(event1)
                }
            } else {
                Log.d(TAG, "$source datos: null")
            }
        }
        .addOnFailureListener { exception ->
            Log.e(TAG, "Error al recuperar los eventos: $exception")
        }
    }
}
