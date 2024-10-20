package com.example.redcolaboracion.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.RequestedHelp
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class RequestedHelpListViewModel: ViewModel() {
    var uiRequestedHelpList = mutableStateListOf<RequestedHelp>()
    val TAG = "RequestedHelpViewModel"

    fun readEvent(userId: String? = null, category: String? = null) {
        val db = Firebase.firestore
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        var query: Query = db.collection("requestedHelp")

        // Agrega filtros dinámicamente si los parámetros no son nulos
        if (userId != null) {
            query = query.whereEqualTo("userId", userId)
        }
        if (category != null) {
            query = query.whereEqualTo("category", category)
        }

        // Ejecutar la consulta
        query.get().addOnSuccessListener { documents ->
            val source = if (documents != null && documents.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            uiRequestedHelpList.clear()
            for (doc in documents) {
                val doc_requestmessage = doc["message"].toString()
                val doc_requestdate_stamp: Timestamp = doc["requestDate"] as Timestamp
                val doc_requestdate = formato.format(doc_requestdate_stamp.toDate()).toString()
                val doc_category = doc["category"].toString()
                val doc_priority = doc["priority"].toString()
                val doc_status = doc["status"].toString()
                val doc_efectiveHelp = doc["efectiveHelp"].toString()
                val doc_efectiveDate_stamp: Timestamp = doc["efectiveDate"] as Timestamp
                val doc_efectiveDate = formato.format(doc_efectiveDate_stamp.toDate()).toString()
                val RequestedHelp1 = RequestedHelp(
                    requestMessage = doc_requestmessage,
                    requestDate = doc_requestdate,
                    category = doc_category,
                    priority = doc_priority,
                    status = doc_status,
                    efectiveHelp = doc_efectiveHelp,
                    efectiveDate = doc_efectiveDate,
                )
                uiRequestedHelpList.add(RequestedHelp1)
            }
        }
    }
}