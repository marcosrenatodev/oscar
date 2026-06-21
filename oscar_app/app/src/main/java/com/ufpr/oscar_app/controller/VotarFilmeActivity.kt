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
import com.ufpr.oscar_app.model.Filme

class VotarFilmeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_votar_filme)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        @Suppress("DEPRECATION")
        val filme = intent.getSerializableExtra("filme") as? Filme
        if (filme == null) {
            finish()
            return
        }

        val poster = findViewById<ImageView>(R.id.posterImageView)
        findViewById<TextView>(R.id.nomeTextView).text = filme.nome
        findViewById<TextView>(R.id.generoTextView).text = filme.genero

        Picasso.get()
            .load(filme.foto)
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.placeholder_poster)
            .into(poster)

        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        findViewById<Button>(R.id.votarButton).setOnClickListener { view ->
            // Voto registrado apenas localmente; o envio real ocorre na tela Confirmar Voto.
            Snackbar.make(
                view,
                "Voto registrado — você pode trocar até a confirmação final",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
