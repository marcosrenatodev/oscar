package com.ufpr.oscar_app.model

data class VotoLocal(
    val usuarioId: Int,
    val filmeId: String?,
    val filmeNome: String?,
    val filmeGenero: String?,
    val filmeFoto: String?,
    val diretorId: String?,
    val diretorNome: String?,
    val confirmado: Boolean
)
