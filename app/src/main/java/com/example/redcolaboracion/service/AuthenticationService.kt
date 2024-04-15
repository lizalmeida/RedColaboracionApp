package com.example.redcolaboracion.service

interface AuthenticationService {
    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onComplete: (String?) -> Unit
    )
}