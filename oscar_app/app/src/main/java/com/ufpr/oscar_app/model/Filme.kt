package com.ufpr.oscar_app.model

import java.io.Serializable

data class Filme(
    val id: String,
    val nome: String,
    val genero: String,
    val foto: String
) : Serializable
