package com.ufpr.oscar_app.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ufpr.oscar_app.R
import com.ufpr.oscar_app.adapter.FilmeAdapter
import com.ufpr.oscar_app.model.Filme
import com.ufpr.oscar_app.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaFilmesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var erroTextView: TextView

    private var usuarioId: Int = -1
    private var usuarioNome: String? = null
    private var usuarioLogin: String? = null
    private var token: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_filmes)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            bottomNav.setPadding(0, 0, 0, bars.bottom)
            insets
        }

        // Dados do usuário repassados para manter a sessão entre as abas
        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioNome = intent.getStringExtra("usuarioNome")
        usuarioLogin = intent.getStringExtra("usuarioLogin")
        token = intent.getIntExtra("token", -1)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        erroTextView = findViewById(R.id.erroTextView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        configurarBottomNav(bottomNav)
        carregarFilmes()
    }

    private fun configurarBottomNav(bottomNav: BottomNavigationView) {
        bottomNav.selectedItemId = R.id.nav_filme
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_filme -> true
                R.id.nav_inicio -> {
                    abrirAba(BoasvindasActivity::class.java)
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

    private fun carregarFilmes() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        erroTextView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val filmes = withContext(Dispatchers.IO) {
                    RetrofitClient.api.listarFilmes()
                }
                exibirFilmes(filmes)
            } catch (e: Exception) {
                erroTextView.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun exibirFilmes(filmes: List<Filme>) {
        if (filmes.isEmpty()) {
            erroTextView.text = "Nenhum filme disponível no momento."
            erroTextView.visibility = View.VISIBLE
            return
        }

        recyclerView.adapter = FilmeAdapter(filmes) { filme ->
            val intent = Intent(this, VotarFilmeActivity::class.java)
            intent.putExtra("filme", filme)
            startActivity(intent)
        }
        recyclerView.visibility = View.VISIBLE
    }
}
