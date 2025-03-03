package com.example.redcolaboracion.viewmodel

import android.util.Log
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
    val TAG = "GivedHelpViewModel"

    var uiGivedHelpList = mutableStateListOf<RequestedHelp>()

    fun readEvent() {
        val db = Firebase.firestore
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        db.collection("givedHelp")
            .whereEqualTo("uidUser", UserSession.userId)
            .get()
            .addOnSuccessListener { result ->

            uiGivedHelpList.clear()
            for (doc in result) {
                val docUidRequestedHelp = doc["uidRequestedHelp"].toString()

                db.collection("requestedHelp").document(docUidRequestedHelp).get()
                    .addOnSuccessListener { requestedDoc ->
                        Log.i(TAG, "Lista de ayudas recuperadas con éxito.")

                        val docUidUser = requestedDoc["userId"].toString()
                        db.collection("users").document(docUidUser).get()
                            .addOnSuccessListener { userDoc ->
                                val docRequestedUser = userDoc["name"].toString() + " " + userDoc["lastname"].toString()
                                val docRequestmessage = requestedDoc["message"].toString()
                                val docRequestdateStamp: Timestamp = requestedDoc["requestDate"] as Timestamp
                                val docRequestdate = formato.format(docRequestdateStamp.toDate()).toString()
                                val docCategory = requestedDoc["category"].toString()
                                val docPriority = requestedDoc["priority"].toString()
                                val docStatus = requestedDoc["status"].toString()
                                val docEfectiveHelp = requestedDoc["efectiveHelp"].toString()
                                val docEfectiveDateStamp: Timestamp = requestedDoc["efectiveDate"] as Timestamp
                                val docEfectiveDate = formato.format(docEfectiveDateStamp.toDate()).toString()
                                val requestedHelp1 = RequestedHelp(
                                    requestMessage = docRequestmessage,
                                    requestDate = docRequestdate,
                                    category = docCategory,
                                    priority = docPriority,
                                    status = docStatus,
                                    efectiveHelp = docEfectiveHelp,
                                    efectiveDate = docEfectiveDate,
                                    requestUser = docRequestedUser,
                                    requestUserId = docUidUser
                                )
                                uiGivedHelpList.add(requestedHelp1)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al obtener las ayudas realizadas. ", exception)
            }
    }
}
