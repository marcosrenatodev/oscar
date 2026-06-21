package com.ufpr.oscar_app.model

data class LoginResponse(
    val sucesso: Boolean,
    val mensagem: String,
    val usuario: Usuario?,
    val token: Int?
)