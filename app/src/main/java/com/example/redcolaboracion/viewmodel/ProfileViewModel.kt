package com.example.redcolaboracion.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.LoginUIState
import com.example.redcolaboracion.model.User
import com.example.redcolaboracion.model.UserSession
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
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

                    val db = Firebase.firestore
                    db.collection("users")
                        .document(currentUser?.uid ?: "no-id")
                        .set(user)
                        .addOnSuccessListener {
                            println("Usuario registrado con éxito.")
                            onSuccess()
                            UserSession.userId = currentUser?.uid
                            println(UserSession.userId)
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

    fun updateUser(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = Firebase.firestore
        db.collection("users").document(UserSession.userId.toString())
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                println("Datos del usuario actualizados con éxito.")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                println("Error al actualizar los datos del usuario: $exception")
                onFailure(exception)
            }
    }

    fun readUser(userId: String) {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(userId)
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

                val email = snapshot.get("email").toString()
                val name = snapshot.get("name").toString()
                val lastname = snapshot.get("lastname").toString()
                val imageUrl = snapshot.get("imageUrl").toString()
                val phone = snapshot.get("phone").toString()
                val address = snapshot.get("address").toString()
                val location = snapshot.get("location").toString()

                uiState.value = LoginUIState(email, name, lastname, imageUrl, phone, address, location)

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
                    UserSession.userId = uid
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

    fun sendPasswordToEmail(email: String) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ProfileViewModel", "Correo de restablecimiento enviado a: $email")
                } else {
                    Log.e("ProfileViewModel", "Error al enviar correo de restablecimiento", task.exception)
                }
            }
    }

}