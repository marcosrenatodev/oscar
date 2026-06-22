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
import com.ufpr.oscar_app.data.dao.VotoLocalDAO
import com.ufpr.oscar_app.model.Filme
import com.ufpr.oscar_app.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaFilmesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var erroTextView: TextView
    private lateinit var votoDAO: VotoLocalDAO
    private var filmeAdapter: FilmeAdapter? = null

    private var usuarioId: Int = -1
    private var usuarioNome: String? = null
    private var usuarioLogin: String? = null
    private var token: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_filmes)
        configurarBarraNavegacao()
        lerExtras()
        referenciarViews()
        carregarFilmes()
    }

    /**
     * Atualiza o selo "SEU VOTO" ao voltar da tela de votar no filme.
     */
    override fun onResume() {
        super.onResume()
        filmeAdapter?.atualizarVotado(votoDAO.buscarPorUsuario(usuarioId)?.filmeId)
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
     * Liga as views da tela, instancia o DAO e configura o RecyclerView.
     */
    private fun referenciarViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        erroTextView = findViewById(R.id.erroTextView)
        votoDAO = VotoLocalDAO(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
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
     * CHAMADA DE API: GET /filmes.
     * Carrega a lista de filmes de forma assíncrona, exibindo a ProgressBar
     * durante o carregamento.
     */
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

    /**
     * BIND: popula o RecyclerView com os filmes, marca o filme votado e abre a tela
     * de voto ao tocar num item.
     */
    private fun exibirFilmes(filmes: List<Filme>) {
        if (filmes.isEmpty()) {
            erroTextView.text = "Nenhum filme disponível no momento."
            erroTextView.visibility = View.VISIBLE
            return
        }

        val votadoId = votoDAO.buscarPorUsuario(usuarioId)?.filmeId
        filmeAdapter = FilmeAdapter(filmes, votadoId) { filme ->
            val intent = Intent(this, VotarFilmeActivity::class.java)
            intent.putExtra("filme", filme)
            intent.putExtra("usuarioId", usuarioId)
            startActivity(intent)
        }
        recyclerView.adapter = filmeAdapter
        recyclerView.visibility = View.VISIBLE
    }
}
