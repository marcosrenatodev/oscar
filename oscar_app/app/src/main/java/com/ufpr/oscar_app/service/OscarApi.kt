package com.ufpr.oscar_app.service

import com.ufpr.oscar_app.model.ConsultaVotoResponse
import com.ufpr.oscar_app.model.Diretor
import com.ufpr.oscar_app.model.Filme
import com.ufpr.oscar_app.model.LoginRequest
import com.ufpr.oscar_app.model.LoginResponse
import com.ufpr.oscar_app.model.VotoRequest
import com.ufpr.oscar_app.model.VotoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OscarApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("filmes")
    suspend fun listarFilmes(): List<Filme>

    @GET("diretores")
    suspend fun listarDiretores(): List<Diretor>

    @POST("votos/confirmar")
    suspend fun confirmarVoto(@Body request: VotoRequest): Response<VotoResponse>

    @GET("usuarios/{id}/voto")
    suspend fun consultarVoto(@Path("id") usuarioId: Int): Response<ConsultaVotoResponse>
}
