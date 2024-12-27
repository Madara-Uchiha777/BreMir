package com.example.bremir.auth

import com.example.bremir.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImpl : IAuthRepository  {
    val auth = FirebaseAuth.getInstance()
    private val usersRef = FirebaseFirestore.getInstance().collection("users")

    override suspend fun register(
        email: String,
        name: String,
        password: String
    ): AuthOperationResult<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid!!
                val user = User(id = uid, username = name).apply {
                    this.email = email
                    this.password = password
                }
                usersRef.document(uid).set(user).await()
                AuthOperationResult.Success(result)
            }
        }
    }

    override suspend fun login(email: String, password: String): AuthOperationResult<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                AuthOperationResult.Success(result)
            }
        }
    }
}

inline fun <T> safeCall(action: () -> AuthOperationResult<T>): AuthOperationResult<T> {
    return try {
        action()
    } catch (e: Exception) {
        AuthOperationResult.Error(e.message ?: "An unknown error occurred")
    }
}