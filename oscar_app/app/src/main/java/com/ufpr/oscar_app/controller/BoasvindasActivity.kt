package com.ufpr.oscar_app.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ufpr.oscar_app.R
import com.ufpr.oscar_app.view.TicketDrawable

class BoasvindasActivity : AppCompatActivity() {

    private var usuarioId: Int = -1
    private var usuarioNome: String? = null
    private var usuarioLogin: String? = null
    private var token: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_boasvindas)
        configurarBarraNavegacao()
        lerExtras()
        exibirDados()
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
        bottomNav.selectedItemId = R.id.nav_inicio
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_filme -> {
                    abrirAba(ListaFilmesActivity::class.java)
                    false
                }
                R.id.nav_diretor -> {
                    abrirAba(VotarDiretorActivity::class.java)
                    false
                }
                R.id.nav_confirmar -> {
                    abrirAba(ConfirmarVotoActivity::class.java)
                    false
                }
                else -> false
            }
        }
    }

    /**
     * Lê os dados do usuário e o token recebidos do login via Intent.
     */
    private fun lerExtras() {
        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioNome = intent.getStringExtra("usuarioNome")
        usuarioLogin = intent.getStringExtra("usuarioLogin")
        token = intent.getIntExtra("token", -1)
    }

    /**
     * BIND: exibe a saudação e o token nos TextViews, aplica o fundo de ticket
     * e liga o botão de logout.
     */
    private fun exibirDados() {
        val primeiroNome = usuarioNome?.trim()?.split(" ")?.firstOrNull().orEmpty()
        findViewById<TextView>(R.id.bemVindoTextView).text =
            if (primeiroNome.isNotEmpty()) "Bem-vindo, $primeiroNome" else "Bem-vindo"
        findViewById<TextView>(R.id.tokenTextView).text = token.toString()

        aplicarFundoTicket()

        findViewById<ImageView>(R.id.logoutButton).setOnClickListener { confirmarLogout() }
    }

    /**
     * Desenha o card do token no formato de ticket de cinema.
     */
    private fun aplicarFundoTicket() {
        val d = resources.displayMetrics.density
        findViewById<View>(R.id.tokenCard).background = TicketDrawable(
            fillColor = ContextCompat.getColor(this, R.color.oscar_surface),
            strokeColor = ContextCompat.getColor(this, R.color.oscar_gold_dark),
            cornerRadius = 16f * d,
            notchRadius = 12f * d,
            strokeWidthPx = 1.5f * d
        )
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
     * Mostra o AlertDialog de confirmação antes de encerrar a sessão.
     */
    private fun confirmarLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sair") { _, _ -> sair() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Encerra a sessão e volta para a tela de login limpando a pilha.
     */
    private fun sair() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
