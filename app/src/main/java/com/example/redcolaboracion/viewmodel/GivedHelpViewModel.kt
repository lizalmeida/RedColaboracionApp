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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GivedHelpViewModel: ViewModel() {
    val firestore: FirebaseFirestore = Firebase.firestore
    //var categories = mutableStateListOf<Category>()
    val TAG = "GivedHelpViewModel"
    var UIRequestedHelp = mutableStateOf(RequestedHelp())

    fun saveGivedHelp(
        uidRequestedHelp: String,
        comments: String,
        offeredDate: Date,
        givedDate: Date,
        uidUser: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val data = mapOf(
                "uidRequestedHelp" to uidRequestedHelp,
                "comments" to comments,
                "offeredDate" to Timestamp(offeredDate),
                "givedDate" to Timestamp(givedDate),
                "uidUser" to uidUser
            )

            firestore.collection("givedHelp")
                .add(data)
                .addOnSuccessListener {
                    println("Ayuda guardada con éxito. Gracias por su colaboración!")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    println("Error al guardar la repuesta: $exception")
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
                val doc_userId = snapshot.get("userId").toString()

                db.collection("users").document(doc_userId).get()
                    .addOnSuccessListener { userDoc ->
                        val doc_requestedUser =
                            userDoc["name"].toString() + " " + userDoc["lastname"].toString()

                        UIRequestedHelp.value = RequestedHelp(
                            doc_id,
                            doc_requestmessage,
                            doc_requestdate,
                            doc_category,
                            doc_priority,
                            doc_status,
                            doc_efectiveHelp,
                            doc_efectiveDate,
                            doc_requestedUser
                        )
                    }
            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }
}