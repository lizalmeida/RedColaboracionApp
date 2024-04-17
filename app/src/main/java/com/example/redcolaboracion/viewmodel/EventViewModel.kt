package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class EventViewModel: ViewModel() {
    val uiEventsList: MutableList<MutableState<Event>> = mutableListOf()
    val TAG = "EventViewModel"
    private lateinit var auth: FirebaseAuth

    fun readEvent() {
        val db = Firebase.firestore
        val docRef = db.collection("events").get()

        docRef.addOnSuccessListener  { documents ->

            val source = if (documents != null && documents.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            for (doc in documents!!) {
                val eventData = doc.data
                val event = Event(
                    title = eventData["title"] as? String ?: "",
                    imageUrl = eventData["imageUrl"] as? String ?: "",
                    content = eventData["content"] as? String ?: "",
                    date = eventData["date"] as? String ?: "",
                    startPublishDate = eventData["startPublishDate"] as? String ?: "",
                    endPublishDate = eventData["endPublishDate"] as? String ?: ""
                )
                println("Titulo: "+event.title)
                val uiEvent = mutableStateOf(event)
                uiEventsList.add(uiEvent)
            }
        }
        println("Termina viewModel")
    }
}
