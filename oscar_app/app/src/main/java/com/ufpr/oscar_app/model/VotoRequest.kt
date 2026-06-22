package com.ufpr.oscar_app.model

data class VotoRequest(
    val usuarioId: Int,
    val filmeId: String,
    val diretorId: String,
    val token: Int
)
