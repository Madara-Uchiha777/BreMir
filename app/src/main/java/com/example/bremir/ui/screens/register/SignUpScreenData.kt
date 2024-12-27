package com.example.bremir.ui.screens.register

import com.example.bremir.model.User

class SignUpScreenData {
    var user: User = User ("", "")
    var passwordAgain: String = ""
    var emailError: String? = null
    var passwordError: String? = null
    var passwordAgainError: String? = null
}