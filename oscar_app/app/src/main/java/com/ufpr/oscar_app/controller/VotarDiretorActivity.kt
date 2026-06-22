package com.ufpr.oscar_app.controller

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.ufpr.oscar_app.R
import com.ufpr.oscar_app.data.dao.VotoLocalDAO
import com.ufpr.oscar_app.model.Diretor
import com.ufpr.oscar_app.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VotarDiretorActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var erroTextView: TextView
    private lateinit var votarButton: Button
    private lateinit var votoDAO: VotoLocalDAO

    private var diretorSelecionado: Diretor? = null
    private var diretorVotadoId: String? = null

    private var usuarioId: Int = -1
    private var usuarioNome: String? = null
    private var usuarioLogin: String? = null
    private var token: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_votar_diretor)
        configurarBarraNavegacao()
        lerExtras()
        referenciarViews()
        carregarDiretores()
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
        bottomNav.selectedItemId = R.id.nav_diretor
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_diretor -> true
                R.id.nav_inicio -> {
                    abrirAba(BoasvindasActivity::class.java)
                    false
                }
                R.id.nav_filme -> {
                    abrirAba(ListaFilmesActivity::class.java)
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
     * Liga as views da tela, instancia o DAO e configura o botão de salvar voto.
     */
    private fun referenciarViews() {
        radioGroup = findViewById(R.id.diretoresRadioGroup)
        progressBar = findViewById(R.id.progressBar)
        erroTextView = findViewById(R.id.erroTextView)
        votarButton = findViewById(R.id.votarButton)
        votoDAO = VotoLocalDAO(this)

        votarButton.isEnabled = false
        votarButton.setOnClickListener { registrarVotoLocal(it) }
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
     * CHAMADA DE API: GET /diretores.
     * Carrega os diretores de forma assíncrona, exibindo a ProgressBar durante o carregamento.
     */
    private fun carregarDiretores() {
        progressBar.visibility = View.VISIBLE
        radioGroup.visibility = View.GONE
        erroTextView.visibility = View.GONE
        votarButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val diretores = withContext(Dispatchers.IO) {
                    RetrofitClient.api.listarDiretores()
                }
                exibirDiretores(diretores)
            } catch (e: Exception) {
                erroTextView.text = "Não foi possível carregar os diretores."
                erroTextView.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    /**
     * BIND: monta o RadioGroup dinamicamente, um RadioButton por diretor retornado,
     * e restaura o voto salvo localmente.
     */
    private fun exibirDiretores(diretores: List<Diretor>) {
        radioGroup.removeAllViews()
        diretorSelecionado = null
        votarButton.isEnabled = false

        if (diretores.isEmpty()) {
            erroTextView.text = "Nenhum diretor disponível no momento."
            erroTextView.visibility = View.VISIBLE
            return
        }

        diretores.forEach { diretor ->
            radioGroup.addView(criarRadioButton(diretor))
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selecionado = group.findViewById<RadioButton>(checkedId)
            diretorSelecionado = selecionado?.tag as? Diretor
            votarButton.isEnabled = diretorSelecionado != null
        }

        radioGroup.visibility = View.VISIBLE
        restaurarVotoLocal()
    }

    /**
     * Cria um RadioButton estilizado: radio à esquerda, nome, estrela vazia à direita
     * e destaque de borda/fundo quando selecionado.
     */
    private fun criarRadioButton(diretor: Diretor): RadioButton {
        val density = resources.displayMetrics.density
        val estados = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        val coresBotao = intArrayOf(
            ContextCompat.getColor(this, R.color.oscar_gold),
            ContextCompat.getColor(this, R.color.oscar_text_secondary)
        )
        val coresTexto = intArrayOf(
            ContextCompat.getColor(this, R.color.oscar_gold),
            ContextCompat.getColor(this, R.color.oscar_text_primary)
        )

        return RadioButton(this).apply {
            id = View.generateViewId()
            tag = diretor
            text = diretor.nome
            textSize = 17f
            gravity = Gravity.CENTER_VERTICAL
            background = ContextCompat.getDrawable(context, R.drawable.bg_radio_item)
            buttonTintList = ColorStateList(estados, coresBotao)
            setTextColor(ColorStateList(estados, coresTexto))
            compoundDrawablePadding = (8 * density).toInt()
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                null, null,
                ContextCompat.getDrawable(context, R.drawable.ic_star_outline),
                null
            )
            setPadding(
                (16 * density).toInt(),
                (16 * density).toInt(),
                (16 * density).toInt(),
                (16 * density).toInt()
            )
            layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (12 * density).toInt()
            }
        }
    }

    /**
     * Lê o voto local salvo e, se houver diretor votado, marca o radio e preenche a estrela.
     */
    private fun restaurarVotoLocal() {
        val votadoId = votoDAO.buscarPorUsuario(usuarioId)?.diretorId ?: return
        diretorVotadoId = votadoId

        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i) as RadioButton
            if ((radioButton.tag as? Diretor)?.id == votadoId) {
                radioButton.isChecked = true
            }
        }
        atualizarEstrelas(votadoId)
    }

    /**
     * Preenche a estrela do diretor votado e deixa as demais vazias.
     */
    private fun atualizarEstrelas(votadoId: String?) {
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i) as RadioButton
            val diretor = radioButton.tag as? Diretor
            val estrela = if (diretor?.id == votadoId) {
                R.drawable.ic_star_filled
            } else {
                R.drawable.ic_star_outline
            }
            radioButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(this, estrela), null
            )
        }
    }

    /**
     * Persiste localmente o voto no diretor selecionado (SQLite) e preenche a estrela dele.
     * O envio real ocorre apenas na tela de confirmação.
     */
    private fun registrarVotoLocal(view: View) {
        val diretor = diretorSelecionado ?: return

        votoDAO.salvarDiretor(usuarioId, diretor)
        diretorVotadoId = diretor.id
        atualizarEstrelas(diretor.id)

        Snackbar.make(
            view,
            "Voto registrado. Você pode trocar até a confirmação final.",
            Snackbar.LENGTH_LONG
        ).show()
    }
}
