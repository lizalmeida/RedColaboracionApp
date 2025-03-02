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
    val firestore: FirebaseFirestore = Firebase.firestore
    //var categories = mutableStateListOf<Category>()
    val TAG = "RequestedHelpViewModel"
    var UIRequestedHelp = mutableStateOf(RequestedHelp())

    fun saveRequestHelp(
        requestMessage: String,
        requestDate: Date,
        category: String,
        priority: Int,
        status: String,
        efectiveHelp: Boolean,
        //efectiveDate: Date?,  // Nullable Date
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
                    println("Solicitud de ayuda guardada con éxito.")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    println("Error al guardar la solicitud: $exception")
                    onFailure(exception)
                }
        }
    }

    fun readRequestedHelp(id: String){
        val db = Firebase.firestore
        val docRef = db.collection("requestedHelp").document(id)
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

            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "$source data: ${snapshot.data}")

                val doc_id = snapshot.get("id").toString()
                val doc_requestmessage = snapshot.get("requestMessage").toString()
                //val doc_requestdate_stamp: Timestamp = snapshot.get("requestDate").toString() as Timestamp
                val doc_requestdate_stamp: Timestamp? = snapshot.get("requestDate") as? Timestamp
                val doc_requestdate = formato.format(doc_requestdate_stamp?.toDate()).toString()
                val doc_category = snapshot.get("category").toString()
                val doc_priority = snapshot.get("priority").toString()
                val doc_status = snapshot.get("status").toString()
                val doc_efectiveHelp = snapshot.get("efectiveHelp").toString()
                //val doc_efectiveDate_stamp: Timestamp = snapshot.get("efectiveDate").toString() as Timestamp
                val doc_efectiveDate_stamp: Timestamp? = snapshot.get("efectiveDate") as? Timestamp
                val doc_efectiveDate = formato.format(doc_efectiveDate_stamp?.toDate()).toString()
                val doc_efectiveComments = snapshot.get("efectiveComments").toString()
                val doc_userId = snapshot.get("userId").toString()

                db.collection("users").document(doc_userId).get()
                    .addOnSuccessListener { userDoc ->
                        val doc_requestedUser =
                            userDoc["name"].toString() + " " + userDoc["lastname"].toString()

                        UIRequestedHelp.value = RequestedHelp(
                            id = doc_id,
                            requestMessage = doc_requestmessage,
                            requestDate = doc_requestdate,
                            category = doc_category,
                            priority = doc_priority,
                            status = doc_status,
                            efectiveHelp = doc_efectiveHelp,
                            efectiveDate = doc_efectiveDate,
                            efectiveComments = doc_efectiveComments,
                            requestUser = doc_requestedUser
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
                    println("Solicitud de ayuda Finalizada")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    println("Error al finalizar la solicitud de ayuda: $exception")
                    onFailure(exception)
                }
        }
    }

    fun notificationToUsers(
        Category: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val db = Firebase.firestore
        val docRef = db.collection("userCategory").document(Category)
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

                val userId = snapshot.get("uidUser").toString()
            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }
}