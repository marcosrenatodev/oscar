package com.ufpr.oscar_app.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ufpr.oscar_app.R

class BoasvindasActivity : AppCompatActivity() {

    private var usuarioId: Int = -1
    private var usuarioNome: String? = null
    private var usuarioLogin: String? = null
    private var token: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_boasvindas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Dados recebidos do login
        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioNome = intent.getStringExtra("usuarioNome")
        usuarioLogin = intent.getStringExtra("usuarioLogin")
        token = intent.getIntExtra("token", -1)

        findViewById<TextView>(R.id.tokenTextView).text = "Token: $token"

        findViewById<Button>(R.id.votarFilmeButton).setOnClickListener {
            abrirTela(VotarFilmeActivity::class.java)
        }

        findViewById<Button>(R.id.votarDiretorButton).setOnClickListener {
            abrirTela(VotarDiretorActivity::class.java)
        }

        findViewById<Button>(R.id.confirmarVotoButton).setOnClickListener {
            abrirTela(ConfirmarVotoActivity::class.java)
        }

        findViewById<Button>(R.id.sairButton).setOnClickListener {
            sair()
        }
    }

    private fun abrirTela(destino: Class<*>) {
        val intent = Intent(this, destino)
        intent.putExtra("usuarioId", usuarioId)
        intent.putExtra("usuarioNome", usuarioNome)
        intent.putExtra("usuarioLogin", usuarioLogin)
        intent.putExtra("token", token)
        startActivity(intent)
    }

    private fun sair() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
