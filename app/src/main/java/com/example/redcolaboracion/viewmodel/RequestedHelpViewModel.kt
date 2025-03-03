package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redcolaboracion.model.RequestedHelp
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RequestedHelpViewModel: ViewModel() {
    val TAG = "RequestedHelpViewModel"
    val firestore: FirebaseFirestore = Firebase.firestore
    var UIRequestedHelp = mutableStateOf(RequestedHelp())

    fun saveRequestHelp(
        requestMessage: String,
        requestDate: Date,
        category: String,
        priority: Int,
        status: String,
        efectiveHelp: Boolean,
        efectiveDate: Date,
        efectiveComments: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val data = mapOf(
                "requestMessage" to requestMessage,
                "requestDate" to Timestamp(requestDate),
                "category" to category,
                "priority" to priority,
                "status" to status,
                "efectiveHelp" to efectiveHelp,
                "efectiveDate" to Timestamp(efectiveDate),
                "efectiveComments" to efectiveComments,
                "userId" to userId
            )

            firestore.collection("requestedHelp")
                .add(data)
                .addOnSuccessListener {
                    Log.i(TAG,"Solicitud de ayuda guardada con éxito.")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG,"Error al guardar la solicitud. ", exception)
                    onFailure(exception)
                }
        }
    }

    fun readRequestedHelp(id: String){
        val db = Firebase.firestore
        val docRef = db.collection("requestedHelp").document(id)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            if (snapshot != null && snapshot.exists()) {
                Log.i(TAG, "$source data: ${snapshot.data}")

                val docId = snapshot.get("id").toString()
                val docRequestmessage = snapshot.get("requestMessage").toString()
                val docRequestdateStamp: Timestamp = snapshot.get("requestDate") as Timestamp
                val docRequestdate = formato.format(docRequestdateStamp.toDate()).toString()
                val docCategory = snapshot.get("category").toString()
                val docPriority = snapshot.get("priority").toString()
                val docStatus = snapshot.get("status").toString()
                val docEfectiveHelp = snapshot.get("efectiveHelp").toString()
                val docEfectiveDateStamp: Timestamp = snapshot.get("efectiveDate") as Timestamp
                val docEfectiveDate = formato.format(docEfectiveDateStamp.toDate()).toString()
                val docEfectiveComments = snapshot.get("efectiveComments").toString()
                val docUserId = snapshot.get("userId").toString()

                db.collection("users").document(docUserId).get()
                    .addOnSuccessListener { userDoc ->
                        val docRequestedUser =
                            userDoc["name"].toString() + " " + userDoc["lastname"].toString()

                        UIRequestedHelp.value = RequestedHelp(
                            id = docId,
                            requestMessage = docRequestmessage,
                            requestDate = docRequestdate,
                            category = docCategory,
                            priority = docPriority,
                            status = docStatus,
                            efectiveHelp = docEfectiveHelp,
                            efectiveDate = docEfectiveDate,
                            efectiveComments = docEfectiveComments,
                            requestUser = docRequestedUser
                        )
                    }
            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }

    fun endedRequestHelp(
        uidRequestedHelp: String,
        efectiveDate: Timestamp?,
        efectiveHelp: Boolean,
        efectiveComments: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val data = mapOf(
                "efectiveHelp" to efectiveHelp,
                "efectiveDate" to efectiveDate,
                "efectiveComments" to efectiveComments,
                "status" to "Finalizada"
            )

            firestore.collection("requestedHelp")
                .document(uidRequestedHelp)
                .update(data)
                .addOnSuccessListener {
                    Log.i(TAG, "Solicitud de ayuda Finalizada")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al finalizar la solicitud de ayuda. ", exception)
                    onFailure(exception)
                }
        }
    }

    fun notificationToUsers(
        category: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val db = Firebase.firestore
        val docRef = db.collection("userCategory").document(category)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            if (snapshot != null && snapshot.exists()) {
                Log.i(TAG, "$source data: ${snapshot.data}")
                val userId = snapshot.get("uidUser").toString()
            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }
}