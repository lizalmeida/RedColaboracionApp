package com.example.redcolaboracion.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.redcolaboracion.model.Category
import com.example.redcolaboracion.model.LoginUIState
import com.example.redcolaboracion.model.User
import com.example.redcolaboracion.model.UserSession
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await


class ProfileViewModel: ViewModel() {
    val TAG = "ProfileViewModel"
    var uiState = mutableStateOf(LoginUIState())
    private lateinit var auth: FirebaseAuth

    fun registerUser(
        user: User,
        email: String,
        password: String,
        selectedCategories: Set<Category>,
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
                            Log.i(TAG, "Usuario registrado con éxito.")
                            UserSession.userId = currentUser?.uid
                            saveUserCategories(UserSession.userId.toString(), selectedCategories) { success ->
                                if (success) {
                                    Log.i(TAG, "Categorías guardadas exitosamente")
                                } else {
                                    Log.e(TAG, "Error al guardar categorías")
                                }
                            }
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Error al registrar el usuario. ", exception)
                            onFailure(exception)
                        }

                } else {
                    Log.e(TAG, "Error al registrar el usuario: ", task.exception)
                }
            }
    }

    fun updateUser(
        user: User,
        selectedCategories: Set<Category>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = Firebase.firestore
        db.collection("users").document(UserSession.userId.toString())
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                saveUserCategories(UserSession.userId.toString(), selectedCategories) { success ->
                    if (success) {
                        Log.i(TAG, "Categorías guardadas exitosamente")
                    } else {
                        Log.e(TAG, "Error al guardar categorías")
                    }
                }
                Log.i(TAG, "Datos del usuario actualizados con éxito.")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al actualizar los datos del usuario. ", exception)
                onFailure(exception)
            }
    }

    fun readUser(userId: String) {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(userId)
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

                val email = snapshot.get("email").toString()
                val name = snapshot.get("name").toString()
                val lastname = snapshot.get("lastname").toString()
                val imageUrl = snapshot.get("imageUrl").toString()
                val phone = snapshot.get("phone").toString()
                val address = snapshot.get("address").toString()
                val location = snapshot.get("location").toString()
                uiState.value = LoginUIState(email, name, lastname, imageUrl, phone, address, location, userId)

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
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val uid = user?.uid
                    onLoginSuccess(uid)
                    UserSession.userId = uid
                    Log.i(TAG, "Usuario ingresa con éxito.")
                } else {
                    val user: FirebaseUser? = auth.currentUser
                    val uid = user?.uid
                    Log.e(TAG, "Usuario ingresa pero con error. Usuario: $uid")

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
                    Log.i(TAG, "Correo de restablecimiento enviado a: $email")
                } else {
                    Log.e(TAG, "Error al enviar correo de restablecimiento", task.exception)
                }
            }
    }

    suspend fun getUserProfileImageUrl(userId: String): String? {
        return try {
            val storageRef = Firebase.storage.reference.child("profile_pictures/$userId.jpg")
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e(TAG, "Referencia: No recupera el archivo de imagen", e)
            null
        }
    }

    fun uploadImageToFirebase(photoUri: Uri, userId: String, onResult: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("profile_pictures/$userId.jpg")
        storageRef.putFile(photoUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onResult("Upload successful! URL: $downloadUrl")
                }
            }
            .addOnFailureListener { exception ->
                onResult("Upload failed: ${exception.message}")
            }
            //.await()
            //storageRef.downloadUrl.await().toString()
        storageRef.downloadUrl.toString()
    }

    fun saveUserCategories(userId: String, selectedCategories: Set<Category>, onResult: (Boolean) -> Unit) {
        val firestore: FirebaseFirestore = Firebase.firestore
        val userCategoryCollection = firestore.collection("userCategory")

        deleteUserCategories(userId) { success ->
            if (success) {
                Log.i(TAG, "Categorías eliminadas correctamente.")
                val batch = firestore.batch()
                selectedCategories.forEach { category ->
                    val docRef = userCategoryCollection.document()
                    val data = mapOf(
                        "category" to category.name,
                        "uidUser" to userId
                    )
                    batch.set(docRef, data)
                }

                batch.commit()
                    .addOnSuccessListener {
                        onResult(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileViewModel", "Error al guardar categorías", e)
                        onResult(false)
                    }
            } else {
                Log.e(TAG, "Error al eliminar las categorías del usuario.")
            }
        }
    }
}

fun deleteUserCategories(userId: String, onResult: (Boolean) -> Unit) {
    val firestore: FirebaseFirestore = Firebase.firestore
    val userCategoryCollection = firestore.collection("userCategory")

    userCategoryCollection
        .whereEqualTo("uidUser", userId)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {

                val batch = firestore.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        onResult(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileViewModel", "Error al eliminar categorías", e)
                        onResult(false)
                    }
            } else {
                // No se encontraron categorías para el usuario
                onResult(true)
            }
        }
        .addOnFailureListener { e ->
            Log.e("ProfileViewModel", "Error al buscar categorías del usuario", e)
            onResult(false)
        }
}