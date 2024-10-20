package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.LoginUIState
import com.example.redcolaboracion.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class ProfileViewModel: ViewModel() {
    var uiState = mutableStateOf(LoginUIState())
    val TAG = "LoginViewModel"
    private lateinit var auth: FirebaseAuth

    fun registerUser(
        user: User,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    //saveUser(currentUser?.uid ?: "no-id", user)
                    val db = Firebase.firestore
                    db.collection("users")
                        .document(currentUser?.uid ?: "no-id")
                        .set(user)
                        .addOnSuccessListener {
                            println("Usuario registrado con éxito.")
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            println("Error al registrar el usuario: $exception")
                            onFailure(exception)
                        }

                } else {
                    Log.w(
                        TAG,
                        "registerUser:failure",
                        task.exception
                    )
                }
            }
    }

    fun readUser() {
        val db = Firebase.firestore
        val docRef = db.collection("users").document("KhpDvVx0RRZLU5UEeJ487CjUDJ43")
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

                val name = snapshot.get("name").toString()
                val lastname = snapshot.get("lastname").toString()
                val imageUrl = snapshot.get("imageUrl").toString()
                val phone = snapshot.get("phone").toString()
                val address = snapshot.get("address").toString()
                val location = snapshot.get("location").toString()

                uiState.value = LoginUIState(name, lastname, imageUrl, phone, address, location)

            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }

    fun readLogin(
        email: String,
        password: String,
        onLoginSuccess: (String?) -> Unit,
        onLoginFailed: (String?) -> Unit
    ) {
        println(email + password)
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val uid = user?.uid
                    onLoginSuccess(uid) // Return UID
                    println("Usuario ingresa con éxito." + uid)
                } else {
                    // Handle sign-in failure
                    val user: FirebaseUser? = auth.currentUser
                    val uid = user?.uid
                    println("Usuario ingresa pero con error." + uid)

                    val error = task.exception?.localizedMessage ?: "Error desconocido"
                    onLoginFailed(error)
                }
            }
    }
}