package com.example.bremir.auth

import com.google.firebase.auth.AuthResult

interface IAuthRepository {
    suspend fun register(email: String, name: String, password: String): AuthOperationResult<AuthResult>
    suspend fun login(email: String, password: String): AuthOperationResult<AuthResult>
}