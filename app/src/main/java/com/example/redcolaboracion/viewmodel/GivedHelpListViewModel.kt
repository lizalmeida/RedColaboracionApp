package com.example.redcolaboracion.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.RequestedHelp
import com.example.redcolaboracion.model.UserSession
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class GivedHelpListViewModel: ViewModel() {
    var uiGivedHelpList = mutableStateListOf<RequestedHelp>()
    val TAG = "GivedHelpViewModel"

    fun readEvent() {
        val db = Firebase.firestore
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        db.collection("givedHelp")
            .whereEqualTo("uidUser", UserSession.userId)
            .get()
            .addOnSuccessListener { result ->
            val source = if (result != null && result.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            uiGivedHelpList.clear()
            for (doc in result) {
                val doc_uidRequestedHelp = doc["uidRequestedHelp"].toString()

                db.collection("requestedHelp").document(doc_uidRequestedHelp).get()
                    .addOnSuccessListener { requestedDoc ->

                        val doc_uidUser = requestedDoc["uidUser"].toString()
                        db.collection("users").document(doc_uidUser).get()
                            .addOnSuccessListener { userDoc ->
                                val doc_requestedUser = userDoc["name"].toString() + " " + userDoc["lastname"].toString()
                                val doc_requestmessage = requestedDoc["message"].toString()
                                val doc_requestdate_stamp: Timestamp = requestedDoc["requestDate"] as Timestamp
                                val doc_requestdate = formato.format(doc_requestdate_stamp.toDate()).toString()
                                val doc_category = requestedDoc["category"].toString()
                                val doc_priority = requestedDoc["priority"].toString()
                                val doc_status = requestedDoc["status"].toString()
                                val doc_efectiveHelp = requestedDoc["efectiveHelp"].toString()
                                val doc_efectiveDate_stamp: Timestamp = requestedDoc["efectiveDate"] as Timestamp
                                val doc_efectiveDate = formato.format(doc_efectiveDate_stamp.toDate()).toString()
                                val RequestedHelp1 = RequestedHelp(
                                    requestMessage = doc_requestmessage,
                                    requestDate = doc_requestdate,
                                    category = doc_category,
                                    priority = doc_priority,
                                    status = doc_status,
                                    efectiveHelp = doc_efectiveHelp,
                                    efectiveDate = doc_efectiveDate,
                                    requestUser = doc_requestedUser
                                )
                                uiGivedHelpList.add(RequestedHelp1)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener las ayudas realizadas: $exception")
            }
    }
}
