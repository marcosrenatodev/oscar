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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Insets: conteúdo respeita topo/laterais; a barra inferior cola no fundo
        // e recebe o padding da barra de sistema dentro dela mesma.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            bottomNav.setPadding(0, 0, 0, bars.bottom)
            insets
        }

        // Dados recebidos do login
        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioNome = intent.getStringExtra("usuarioNome")
        usuarioLogin = intent.getStringExtra("usuarioLogin")
        token = intent.getIntExtra("token", -1)

        val primeiroNome = usuarioNome?.trim()?.split(" ")?.firstOrNull().orEmpty()
        findViewById<TextView>(R.id.bemVindoTextView).text =
            if (primeiroNome.isNotEmpty()) "Bem-vindo, $primeiroNome" else "Bem-vindo"
        findViewById<TextView>(R.id.tokenTextView).text = token.toString()

        aplicarFundoTicket()

        findViewById<ImageView>(R.id.logoutButton).setOnClickListener { confirmarLogout() }

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

    @Suppress("DEPRECATION")
    private fun abrirAba(destino: Class<*>) {
        val intent = Intent(this, destino)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra("usuarioId", usuarioId)
        intent.putExtra("usuarioNome", usuarioNome)
        intent.putExtra("usuarioLogin", usuarioLogin)
        intent.putExtra("token", token)
        startActivity(intent)
        // overridePendingTransition é a API correta para troca de aba sem animação
        // em API 29 (substituto só existe na API 34+).
        overridePendingTransition(0, 0)
    }

    private fun confirmarLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sair") { _, _ -> sair() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun sair() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
