package com.example.bremir.auth

sealed class AuthOperationResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : AuthOperationResult<T>(data)
    class Error<T>(message: String, data: T? = null) : AuthOperationResult<T>(data, message)
    class Loading<T>(data: T? = null) : AuthOperationResult<T>(data)
}