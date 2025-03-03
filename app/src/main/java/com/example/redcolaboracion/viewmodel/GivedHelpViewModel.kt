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
import java.util.Locale

class GivedHelpViewModel: ViewModel() {
    private val TAG = "GivedHelpViewModel"
    private val firestore: FirebaseFirestore = Firebase.firestore
    var UIRequestedHelp = mutableStateOf(RequestedHelp())

    fun saveGivedHelp(
        uidRequestedHelp: String,
        comments: String,
        offeredDate: Timestamp,
        uidUser: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val data = mapOf(
                "uidRequestedHelp" to uidRequestedHelp,
                "comments" to comments,
                "offeredDate" to offeredDate,
                "givedDate" to offeredDate,
                "uidUser" to uidUser
            )

            firestore.collection("givedHelp")
                .add(data)
                .addOnSuccessListener {
                    Log.i(TAG, "givedHelp guardado con éxito.")
                    onSuccess()

                    updateRequestHelp(uidRequestedHelp,
                        onSuccess = {
                            Log.i(TAG, "RequestHelp actualizado con éxito.")
                            onSuccess()
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Error al actualizar RequestHelp. ", exception)
                            onFailure(exception)
                        }
                    )
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al guardar givedHelp. ", exception)
                    onFailure(exception)
                }
        }
    }

    fun readRequestedHelp(id: String){
        val db = Firebase.firestore
        val docRef = db.collection("requestedHelp").document(id)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Error al recuperar requestedHelp", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                "Local"
            } else {
                "Server"
            }

            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            if (snapshot != null && snapshot.exists()) {
                Log.i(TAG, "Datos recuperados: $source data: ${snapshot.data}")

                val docId = snapshot.get("id").toString()
                val docRequestmessage = snapshot.get("requestMessage").toString()
                val docRequestdateStamp: Timestamp? = snapshot.get("requestDate") as? Timestamp
                val docRequestdate = formato.format(docRequestdateStamp?.toDate()!!).toString()
                val docCategory = snapshot.get("category").toString()
                val docPriority = snapshot.get("priority").toString()
                val docStatus = snapshot.get("status").toString()
                val docEfectiveHelp = snapshot.get("efectiveHelp").toString()
                val docEfectiveDateStamp: Timestamp? = snapshot.get("efectiveDate") as? Timestamp
                val docEfectiveDate = formato.format(docEfectiveDateStamp?.toDate()!!).toString()
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
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error al recuperar datos del usuario: $exception")
                    }
            } else {
                Log.d(TAG, "$source datos: null")
            }
        }
    }

    fun updateRequestHelp(
        uidRequestedHelp: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val data = mapOf(
                "status" to "En curso"
            )

            firestore.collection("requestedHelp")
                .document(uidRequestedHelp)
                .update(data)
                .addOnSuccessListener {
                    Log.i(TAG, "Solicitud de ayuda actualizada a En curso")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al actualizar el estado de la solicitud de ayuda: $exception")
                    onFailure(exception)
                }
        }
    }
}