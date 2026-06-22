package com.ufpr.oscar_app.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.ufpr.oscar_app.R
import com.ufpr.oscar_app.model.LoginRequest
import com.ufpr.oscar_app.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {

    private lateinit var editLogin: EditText
    private lateinit var editSenha: EditText
    private lateinit var textErroLogin: TextView
    private lateinit var buttonEntrar: Button
    private lateinit var progressLogin: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        configurarInsets()
        referenciarViews()
    }

    /**
     * Aplica o padding das barras do sistema na raiz da tela.
     */
    private fun configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    /**
     * Liga as views do formulario de login aos campos da classe.
     */
    private fun referenciarViews() {
        editLogin = findViewById(R.id.editLogin)
        editSenha = findViewById(R.id.editSenha)
        textErroLogin = findViewById(R.id.textErroLogin)
        buttonEntrar = findViewById(R.id.buttonEntrar)
        progressLogin = findViewById(R.id.progressLogin)
    }

    /**
     * CHAMADA DE API: POST auth/login.
     * Valida os campos, envia login e senha e, no sucesso, abre a tela de boas-vindas
     * repassando os dados do usuario e o token. Acionada pelo botao Entrar (onClick).
     */
    fun login(view: View) {
        val login = editLogin.text.toString().trim()
        val senha = editSenha.text.toString().trim()

        if (!validarCampos(login, senha)) {
            return
        }

        textErroLogin.visibility = View.GONE
        progressLogin.visibility = View.VISIBLE
        buttonEntrar.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.login(LoginRequest(login, senha))
                }

                if (response.isSuccessful && response.body()?.sucesso == true) {
                    val body = response.body()

                    val intent = Intent(this@AuthActivity, BoasvindasActivity::class.java)
                    intent.putExtra("usuarioId", body?.usuario?.id)
                    intent.putExtra("usuarioNome", body?.usuario?.nome)
                    intent.putExtra("usuarioLogin", body?.usuario?.login)
                    intent.putExtra("token", body?.token)

                    startActivity(intent)
                    finish()
                } else {
                    mostrarErro("Login ou senha inválidos")
                }
            } catch (e: Exception) {
                mostrarErro("Erro ao conectar com o servidor")
            } finally {
                progressLogin.visibility = View.GONE
                buttonEntrar.isEnabled = true
            }
        }
    }

    /**
     * Exibe uma mensagem de erro abaixo do formulario.
     */
    private fun mostrarErro(mensagem: String) {
        textErroLogin.text = mensagem
        textErroLogin.visibility = View.VISIBLE
    }

    /**
     * Valida que login e senha nao estao em branco antes do envio.
     */
    private fun validarCampos(login: String, senha: String): Boolean {
        var valido = true

        if (login.isBlank()) {
            editLogin.error = "Informe o login"
            valido = false
        }

        if (senha.isBlank()) {
            editSenha.error = "Informe a senha"
            valido = false
        }

        if (!valido) {
            mostrarErro("Preencha todos os campos")
        }

        return valido
    }
}
