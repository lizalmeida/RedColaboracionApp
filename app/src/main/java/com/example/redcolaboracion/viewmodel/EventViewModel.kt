package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp

class EventViewModel: ViewModel() {
    var uiEventsList = mutableStateListOf<Event>()
    val TAG = "EventViewModel"

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

            for (doc in documents) {
                val doc_id = doc["id"].toString()
                val doc_title = doc["title"].toString()
                val doc_content = doc["content"].toString()
                val doc_imageUrl = doc["imageUrl"].toString()
                val timestampFromFirestore: Timestamp = doc["date"] as Timestamp
                val doc_date = formato.format(timestampFromFirestore.toDate()).toString() //doc["date"].toString()
                val event1 = Event(
                    id = doc_id,
                    title = doc_title,
                    content = doc_content,
                    imageUrl = doc_imageUrl,
                    date = doc_date,
                    startPublishDate = "",
                    endPublishDate = "")
                uiEventsList.add(event1)
            }
        }
        //println("Titulo1: " + dsEventList[0].title)
    }
}
