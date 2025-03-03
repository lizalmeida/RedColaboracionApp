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
    val TAG = "RequestedHelpListViewModel"
    var uiRequestedHelpList = mutableStateListOf<RequestedHelp>()

    fun readEvent(userId: String? = null, category: String? = null) {
        val db = Firebase.firestore
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        var query: Query = db.collection("requestedHelp")

        if (userId != null) {
            query = query.whereEqualTo("userId", userId)
        }
        if (category != null) {
            query = query.whereEqualTo("category", category)
            query = query.whereEqualTo("status", "Solicitada")
        }

        query.get().addOnSuccessListener { documents ->

            uiRequestedHelpList.clear()
            for (doc in documents) {
                val docId = doc.id
                val docRequestmessage = doc["message"].toString()
                val docRequestdateStamp: Timestamp = doc["requestDate"] as Timestamp
                val docRequestdate = formato.format(docRequestdateStamp.toDate()).toString()
                val docCategory = doc["category"].toString()
                val docPriority = doc["priority"].toString()
                val docStatus = doc["status"].toString()
                val docEfectiveHelp = doc["efectiveHelp"].toString()
                val docEfectiveDateStamp: Timestamp = doc["efectiveDate"] as Timestamp
                val docEfectiveDate = formato.format(docEfectiveDateStamp.toDate()).toString()
                val docUserId = doc["userId"].toString()

                db.collection("users").document(docUserId).get()
                    .addOnSuccessListener { userDoc ->
                        val docRequestedUser =
                            userDoc["name"].toString() + " " + userDoc["lastname"].toString()

                        val requestedHelp1 = RequestedHelp(
                            id = docId,
                            requestMessage = docRequestmessage,
                            requestDate = docRequestdate,
                            category = docCategory,
                            priority = docPriority,
                            status = docStatus,
                            efectiveHelp = docEfectiveHelp,
                            efectiveDate = docEfectiveDate,
                            requestUser = docRequestedUser
                        )
                        uiRequestedHelpList.add(requestedHelp1)
                    }
            }
        }
    }
}