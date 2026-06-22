package com.ufpr.oscar_app.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import com.ufpr.oscar_app.R
import com.ufpr.oscar_app.data.dao.VotoLocalDAO
import com.ufpr.oscar_app.model.VotoRequest
import com.ufpr.oscar_app.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmarVotoActivity : AppCompatActivity() {

    private lateinit var filmePoster: ImageView
    private lateinit var filmeNomeTextView: TextView
    private lateinit var filmeConfirmadoLabel: TextView
    private lateinit var diretorNomeTextView: TextView
    private lateinit var diretorConfirmadoLabel: TextView
    private lateinit var tokenLayout: TextInputLayout
    private lateinit var tokenEditText: TextInputEditText
    private lateinit var confirmarButton: MaterialButton
    private lateinit var progressConfirmar: ProgressBar
    private lateinit var votoDAO: VotoLocalDAO

    private var usuarioId: Int = -1
    private var usuarioNome: String? = null
    private var usuarioLogin: String? = null
    private var token: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmar_voto)
        configurarBarraNavegacao()
        lerExtras()
        referenciarViews()
        sincronizarConfirmacao()
    }

    /**
     * Relê os votos do banco sempre que a tela volta ao primeiro plano, garantindo que
     * a confirmação reflita o que foi votado mesmo quando a Activity é reaproveitada.
     */
    override fun onResume() {
        super.onResume()
        exibirVotos()
        if (votoDAO.buscarPorUsuario(usuarioId)?.confirmado == true) {
            travar()
        }
    }

    /**
     * Aplica o padding das barras do sistema e configura a barra de navegação inferior.
     */
    private fun configurarBarraNavegacao() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            bottomNav.setPadding(0, 0, 0, bars.bottom)
            insets
        }
        bottomNav.selectedItemId = R.id.nav_confirmar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_confirmar -> true
                R.id.nav_inicio -> {
                    abrirAba(BoasvindasActivity::class.java)
                    false
                }
                R.id.nav_filme -> {
                    abrirAba(ListaFilmesActivity::class.java)
                    false
                }
                R.id.nav_diretor -> {
                    abrirAba(VotarDiretorActivity::class.java)
                    false
                }
                else -> false
            }
        }
    }

    /**
     * Lê os dados do usuário e o token recebidos via Intent.
     */
    private fun lerExtras() {
        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioNome = intent.getStringExtra("usuarioNome")
        usuarioLogin = intent.getStringExtra("usuarioLogin")
        token = intent.getIntExtra("token", -1)
    }

    /**
     * Liga as views da tela, instancia o DAO e configura o botão de confirmar.
     */
    private fun referenciarViews() {
        filmePoster = findViewById(R.id.filmePosterImageView)
        filmeNomeTextView = findViewById(R.id.filmeNomeTextView)
        filmeConfirmadoLabel = findViewById(R.id.filmeConfirmadoLabel)
        diretorNomeTextView = findViewById(R.id.diretorNomeTextView)
        diretorConfirmadoLabel = findViewById(R.id.diretorConfirmadoLabel)
        tokenLayout = findViewById(R.id.tokenLayout)
        tokenEditText = findViewById(R.id.tokenEditText)
        confirmarButton = findViewById(R.id.confirmarButton)
        progressConfirmar = findViewById(R.id.progressConfirmar)
        votoDAO = VotoLocalDAO(this)

        confirmarButton.setOnClickListener { confirmarVoto() }
    }

    /**
     * Abre outra aba reaproveitando a Activity existente e repassando a sessão.
     */
    @Suppress("DEPRECATION")
    private fun abrirAba(destino: Class<*>) {
        val intent = Intent(this, destino)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra("usuarioId", usuarioId)
        intent.putExtra("usuarioNome", usuarioNome)
        intent.putExtra("usuarioLogin", usuarioLogin)
        intent.putExtra("token", token)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    /**
     * BIND: exibe o filme e o diretor votados, lidos da persistência local.
     */
    private fun exibirVotos() {
        val voto = votoDAO.buscarPorUsuario(usuarioId)

        filmeNomeTextView.text = voto?.filmeNome ?: voto?.filmeId ?: "Nenhum filme votado"
        diretorNomeTextView.text = voto?.diretorNome ?: voto?.diretorId ?: "Nenhum diretor votado"

        if (!voto?.filmeFoto.isNullOrBlank()) {
            Picasso.get()
                .load(voto?.filmeFoto)
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(filmePoster)
        }
    }

    /**
     * CHAMADA DE API: GET /usuarios/{id}/voto.
     * Verifica no servidor se o usuário já confirmou. Em caso positivo, grava o estado
     * localmente e bloqueia a edição.
     */
    private fun sincronizarConfirmacao() {
        if (votoDAO.buscarPorUsuario(usuarioId)?.confirmado == true) {
            travar()
        }

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.consultarVoto(usuarioId)
                }
                val corpo = response.body()
                if (response.isSuccessful && corpo?.jaVotou == true) {
                    votoDAO.marcarConfirmadoComVotos(usuarioId, corpo.voto?.filmeId, corpo.voto?.diretorId)
                    exibirVotos()
                    travar()
                }
            } catch (e: Exception) {
                // Sem conexão: mantém o estado local.
            }
        }
    }

    /**
     * CHAMADA DE API: POST /votos/confirmar.
     * Valida o voto e o token, envia ao servidor e trata sucesso ou erro via AlertDialog.
     */
    private fun confirmarVoto() {
        val voto = votoDAO.buscarPorUsuario(usuarioId)
        val filmeId = voto?.filmeId
        val diretorId = voto?.diretorId

        if (filmeId.isNullOrBlank() || diretorId.isNullOrBlank()) {
            mostrarAlerta(
                "Voto incompleto",
                "Você precisa votar em um filme e em um diretor antes de confirmar."
            )
            return
        }

        val tokenTexto = tokenEditText.text.toString().trim()
        if (tokenTexto.isEmpty()) {
            tokenLayout.error = "Informe o token"
            return
        }
        val tokenNumero = tokenTexto.toIntOrNull()
        if (tokenNumero == null) {
            tokenLayout.error = "Token inválido"
            return
        }
        tokenLayout.error = null

        enviarVoto(filmeId, diretorId, tokenNumero)
    }

    /**
     * Envia o voto ao servidor e atualiza a tela conforme a resposta.
     */
    private fun enviarVoto(filmeId: String, diretorId: String, tokenNumero: Int) {
        mostrarCarregando(true)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.confirmarVoto(
                        VotoRequest(usuarioId, filmeId, diretorId, tokenNumero)
                    )
                }
                mostrarCarregando(false)

                if (response.isSuccessful) {
                    votoDAO.marcarConfirmado(usuarioId)
                    travar()
                    mostrarAlerta("Voto confirmado", "Seu voto foi registrado com sucesso!")
                } else {
                    if (response.code() == 409) {
                        votoDAO.marcarConfirmado(usuarioId)
                        travar()
                    }
                    mostrarAlerta("Não foi possível confirmar", mensagemDeErro(response.code()))
                }
            } catch (e: Exception) {
                mostrarCarregando(false)
                mostrarAlerta(
                    "Erro de conexão",
                    "Não foi possível enviar seu voto. Verifique a conexão e tente novamente."
                )
            }
        }
    }

    /**
     * Traduz o código HTTP de erro do servidor em uma mensagem para o usuário.
     */
    private fun mensagemDeErro(codigo: Int): String {
        return when (codigo) {
            401 -> "Token inválido. Verifique o código e tente novamente."
            409 -> "Você já registrou seu voto."
            400 -> "Dados inválidos. Verifique seu voto e o token informado."
            else -> "Não foi possível confirmar o voto. Tente novamente."
        }
    }

    /**
     * Alterna o estado de carregamento durante o envio (spinner no botão).
     */
    private fun mostrarCarregando(carregando: Boolean) {
        progressConfirmar.visibility = if (carregando) View.VISIBLE else View.GONE
        if (carregando) {
            confirmarButton.text = ""
            confirmarButton.isEnabled = false
            tokenLayout.isEnabled = false
        } else {
            confirmarButton.text = "CONFIRMAR VOTO"
            confirmarButton.isEnabled = true
            tokenLayout.isEnabled = true
        }
    }

    /**
     * Bloqueia a edição após a confirmação: trava token e botão e exibe o selo CONFIRMADO.
     */
    private fun travar() {
        tokenLayout.isEnabled = false
        tokenEditText.isEnabled = false
        confirmarButton.isEnabled = false
        confirmarButton.text = "VOTO CONFIRMADO"
        filmeConfirmadoLabel.visibility = View.VISIBLE
        diretorConfirmadoLabel.visibility = View.VISIBLE
    }

    /**
     * Exibe um AlertDialog de feedback ao usuário.
     */
    private fun mostrarAlerta(titulo: String, mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setPositiveButton("OK", null)
            .show()
    }
}
