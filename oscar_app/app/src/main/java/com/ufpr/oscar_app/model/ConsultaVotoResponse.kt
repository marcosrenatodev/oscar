package com.ufpr.oscar_app.model

data class ConsultaVotoResponse(
    val sucesso: Boolean,
    val jaVotou: Boolean,
    val voto: VotoConfirmado?
)

data class VotoConfirmado(
    val filmeId: String?,
    val diretorId: String?
)
