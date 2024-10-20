package com.example.redcolaboracion.viewmodel

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.History
import com.example.redcolaboracion.view.SplashScreen
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryViewModel: ViewModel() {
    var uiHistoryList = mutableStateListOf<History>()
    val TAG = "HistoryViewModel"

    fun readEvent() {
        val db = Firebase.firestore
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val docRef = db.collection("requestedHelp").get()
        docRef.addOnSuccessListener  { documents ->
            val source = if (documents != null && documents.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            uiHistoryList.clear()
            for (doc in documents) {
                val doc_effectiveHelp = doc["effectiveHelp"].toString()
                val doc_category = doc["category"].toString()
                val timestampFromFirestore: Timestamp = doc["requestDate"] as Timestamp
                val doc_date = formato.format(timestampFromFirestore.toDate()).toString()
                val doc_priority = doc["priority"].toString()
                val doc_status = doc["status"].toString()
                val history1 = History(
                    effectiveHelp = doc_effectiveHelp,
                    category = doc_category,
                    date = doc_date,
                    priority = doc_priority,
                    status = doc_status
                )
                uiHistoryList.add(history1)
            }
        }
    }

}

