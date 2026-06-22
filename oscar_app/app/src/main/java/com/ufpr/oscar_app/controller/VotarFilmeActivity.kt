package com.ufpr.oscar_app.controller

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.ufpr.oscar_app.R
import com.ufpr.oscar_app.data.dao.VotoLocalDAO
import com.ufpr.oscar_app.model.Filme

class VotarFilmeActivity : AppCompatActivity() {

    private lateinit var votoDAO: VotoLocalDAO
    private var usuarioId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_votar_filme)
        configurarInsets()

        votoDAO = VotoLocalDAO(this)
        usuarioId = intent.getIntExtra("usuarioId", -1)

        @Suppress("DEPRECATION")
        val filme = intent.getSerializableExtra("filme") as? Filme
        if (filme == null) {
            finish()
            return
        }

        exibirFilme(filme)
        configurarAcoes(filme)
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
     * BIND: exibe nome e gênero nos TextViews e carrega o pôster pela URL com Picasso.
     */
    private fun exibirFilme(filme: Filme) {
        findViewById<TextView>(R.id.nomeTextView).text = filme.nome
        findViewById<TextView>(R.id.generoTextView).text = filme.genero

        Picasso.get()
            .load(filme.foto)
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.placeholder_poster)
            .into(findViewById<ImageView>(R.id.posterImageView))
    }

    /**
     * Configura o botão de voltar e o registro local do voto no filme.
     * Se o voto já foi confirmado, o botão fica bloqueado (bloqueio pós-confirmação).
     */
    private fun configurarAcoes(filme: Filme) {
        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        val votarButton = findViewById<Button>(R.id.votarButton)

        if (votoDAO.buscarPorUsuario(usuarioId)?.confirmado == true) {
            votarButton.isEnabled = false
            votarButton.text = "VOTO CONFIRMADO"
            return
        }

        votarButton.setOnClickListener { view ->
            votoDAO.salvarFilme(usuarioId, filme)
            Snackbar.make(
                view,
                "Voto registrado. Você pode trocar até a confirmação final.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
