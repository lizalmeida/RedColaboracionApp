package com.example.redcolaboracion.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File


class ProfileViewModel: ViewModel() {
    var uiState = mutableStateOf(LoginUIState())
    val TAG = "LoginViewModel"
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
                            println("Usuario registrado con éxito.")
                            UserSession.userId = currentUser?.uid
                            println(UserSession.userId)
                            saveUserCategories(UserSession.userId.toString(), selectedCategories) { success ->
                                if (success) {
                                    // Acción en caso de éxito, como mostrar un mensaje o navegar
                                    Log.d("ComposeScreen", "Categorías guardadas exitosamente")
                                } else {
                                    // Manejo de errores
                                    Log.e("ComposeScreen", "Error al guardar categorías")
                                }
                            }
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
                        // Acción en caso de éxito, como mostrar un mensaje o navegar
                        Log.d("ComposeScreen", "Categorías guardadas exitosamente")
                    } else {
                        // Manejo de errores
                        Log.e("ComposeScreen", "Error al guardar categorías")
                    }
                }
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
                val userId = userId
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

    suspend fun getUserProfileImageUrl(userId: String): String? {
        return try {
            val storageRef = Firebase.storage.reference.child("images/$userId.jpg")
            println("Referencia:" + storageRef)
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            // Manejar la excepción según sea necesario
            println("Referencia: No recupera el archivo de imagen")
            null
        }
    }

    fun uploadImageToFirebase(photoUri: Uri, userId: String, context: Context, onResult: (String) -> Unit) : Unit{
        val storage = FirebaseStorage.getInstance()
        val photoUrl: String? = if (photoUri != null) {
            val storageRef = storage.reference.child("images/$userId.jpg")
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
        } else {
            null
        }
    }

    fun renameProfileImage(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val tempFilePath = "images/foto_perfil.jpg" // Ruta del archivo temporal en el Storage
        val finalFileName = UserSession.userId.toString() + ".jpg" // Nombre final para el archivo
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val tempFileRef = storageRef.child(tempFilePath)
        val finalFilePath = "images/$finalFileName" // Nueva ruta del archivo
        val finalFileRef = storageRef.child(finalFilePath)

        // Copiar el archivo a la nueva ubicación
        tempFileRef.downloadUrl.addOnSuccessListener { uri ->
            finalFileRef.putFile(uri)
                .addOnSuccessListener {
                    // Eliminar el archivo temporal
                    tempFileRef.delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    fun saveUserCategories(userId: String, selectedCategories: Set<Category>, onResult: (Boolean) -> Unit) {
        val firestore: FirebaseFirestore = Firebase.firestore
        val userCategoryCollection = firestore.collection("userCategory")

        deleteUserCategories(userId) { success ->
            if (success) {
                println("Categorías eliminadas correctamente.")
                val batch = firestore.batch()
                selectedCategories.forEach { category ->
                    val docRef = userCategoryCollection.document() // Crear un nuevo documento
                    val data = mapOf(
                        "category" to category.name,
                        "uidUser" to userId
                    )
                    batch.set(docRef, data)
                }
                // Ejecutar el batch para guardar las categorías
                batch.commit()
                    .addOnSuccessListener {
                        onResult(true) // Éxito
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileViewModel", "Error al guardar categorías", e)
                        onResult(false) // Error
                    }
            } else {
                println("Error al eliminar las categorías del usuario.")
            }
        }
        // Iniciar una operación de escritura por cada categoría seleccionada
    }
}

fun deleteUserCategories(userId: String, onResult: (Boolean) -> Unit) {
    val firestore: FirebaseFirestore = Firebase.firestore
    val userCategoryCollection = firestore.collection("userCategory")

    // Buscar los documentos que corresponden al usuario
    userCategoryCollection
        .whereEqualTo("uidUser", userId)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Iniciar una operación de escritura por lotes
                val batch = firestore.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }

                // Ejecutar el batch para eliminar las categorías
                batch.commit()
                    .addOnSuccessListener {
                        onResult(true) // Éxito
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileViewModel", "Error al eliminar categorías", e)
                        onResult(false) // Error
                    }
            } else {
                // No se encontraron categorías para el usuario
                onResult(true)
            }
        }
        .addOnFailureListener { e ->
            Log.e("ProfileViewModel", "Error al buscar categorías del usuario", e)
            onResult(false) // Error
        }
}