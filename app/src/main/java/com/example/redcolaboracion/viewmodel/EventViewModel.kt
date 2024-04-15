package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class EventViewModel: ViewModel() {
    var uiEvent = mutableStateOf(Event())
    val TAG = "EventViewModel"
    private lateinit var auth: FirebaseAuth

    fun readEvent() {
        val db = Firebase.firestore
        val docRef = db.collection("events").document("qNp8A7wMwyr0djIkxdKt")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "$source data: ${snapshot.data}")

                val id = snapshot.get("id").toString()
                val title = snapshot.get("title").toString()
                val date = snapshot.get("date").toString()
                val imageUrl = snapshot.get("imageUrl").toString()
                val content = snapshot.get("content").toString()
                val startPublishDate = snapshot.get("startPublishDate").toString()
                val endPublishDate= snapshot.get("endPublishDate").toString()

                uiEvent.value = Event(id, title, date, imageUrl, content, startPublishDate, endPublishDate)

            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }
}