package com.ufpr.oscar_app.controller

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
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

    private var diretorSelecionado: Diretor? = null

    private var usuarioId: Int = -1
    private var usuarioNome: String? = null
    private var usuarioLogin: String? = null
    private var token: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_votar_diretor)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            bottomNav.setPadding(0, 0, 0, bars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuarioId", -1)
        usuarioNome = intent.getStringExtra("usuarioNome")
        usuarioLogin = intent.getStringExtra("usuarioLogin")
        token = intent.getIntExtra("token", -1)

        radioGroup = findViewById(R.id.diretoresRadioGroup)
        progressBar = findViewById(R.id.progressBar)
        erroTextView = findViewById(R.id.erroTextView)
        votarButton = findViewById(R.id.votarButton)

        votarButton.isEnabled = false
        votarButton.setOnClickListener { view ->
            if (diretorSelecionado != null) {
                // Voto registrado apenas localmente; o envio real ocorre na tela Confirmar Voto.
                Snackbar.make(
                    view,
                    "Voto registrado — você pode trocar até a confirmação final",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        configurarBottomNav(bottomNav)
        carregarDiretores()
    }

    private fun configurarBottomNav(bottomNav: BottomNavigationView) {
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
            val radioButton = criarRadioButton(diretor)
            radioGroup.addView(radioButton)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selecionado = group.findViewById<RadioButton>(checkedId)
            diretorSelecionado = selecionado?.tag as? Diretor
            votarButton.isEnabled = diretorSelecionado != null
        }
        radioGroup.visibility = View.VISIBLE
    }

    private fun criarRadioButton(diretor: Diretor): RadioButton {
        val density = resources.displayMetrics.density
        val checkedStates = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        )
        val tintColors = intArrayOf(
            ContextCompat.getColor(this, R.color.oscar_gold),
            ContextCompat.getColor(this, R.color.oscar_text_secondary)
        )

        return RadioButton(this).apply {
            id = View.generateViewId()
            tag = diretor
            text = diretor.nome
            textSize = 17f
            setTextColor(ContextCompat.getColor(context, R.color.oscar_text_primary))
            buttonTintList = ColorStateList(checkedStates, tintColors)
            setPadding(
                (8 * density).toInt(),
                (14 * density).toInt(),
                (8 * density).toInt(),
                (14 * density).toInt()
            )
            layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}
