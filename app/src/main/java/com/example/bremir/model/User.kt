package com.example.bremir.model

data class User(
    var id: String,
    var username: String
){
    var email: String  = ""
    var password: String = ""
}
