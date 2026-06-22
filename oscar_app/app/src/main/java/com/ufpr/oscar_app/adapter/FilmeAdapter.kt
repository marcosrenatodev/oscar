package com.ufpr.oscar_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.ufpr.oscar_app.R
import com.ufpr.oscar_app.model.Filme

/**
 * Adapter da lista de filmes. Funciona com qualquer quantidade de itens retornada
 * pelo servidor. Destaca o filme votado pelo usuário com borda e selo "SEU VOTO".
 */
class FilmeAdapter(
    private val filmes: List<Filme>,
    private var votadoFilmeId: String?,
    private val onFilmeClick: (Filme) -> Unit
) : RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder>() {

    /**
     * Guarda as views de uma célula da lista.
     */
    class FilmeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView as MaterialCardView
        val poster: ImageView = itemView.findViewById(R.id.posterImageView)
        val nome: TextView = itemView.findViewById(R.id.nomeTextView)
        val genero: TextView = itemView.findViewById(R.id.generoTextView)
        val seuVotoBadge: TextView = itemView.findViewById(R.id.seuVotoBadge)
    }

    /**
     * Infla o layout de uma célula da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return FilmeViewHolder(view)
    }

    /**
     * BIND: preenche nome e gênero, carrega o pôster (Picasso) e marca o filme votado.
     */
    override fun onBindViewHolder(holder: FilmeViewHolder, position: Int) {
        val filme = filmes[position]

        holder.nome.text = filme.nome
        holder.genero.text = filme.genero

        Picasso.get()
            .load(filme.foto)
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.placeholder_poster)
            .into(holder.poster)

        marcarVoto(holder, filme.id == votadoFilmeId)

        holder.itemView.setOnClickListener { onFilmeClick(filme) }
    }

    /**
     * Mostra o selo "SEU VOTO" e a borda dourada quando o filme é o votado.
     */
    private fun marcarVoto(holder: FilmeViewHolder, votado: Boolean) {
        holder.seuVotoBadge.visibility = if (votado) View.VISIBLE else View.GONE

        if (votado) {
            val density = holder.itemView.resources.displayMetrics.density
            holder.card.strokeColor = ContextCompat.getColor(holder.itemView.context, R.color.oscar_gold)
            holder.card.strokeWidth = (1.5f * density).toInt()
        } else {
            holder.card.strokeWidth = 0
        }
    }

    /**
     * Atualiza qual filme está votado e redesenha a lista.
     */
    fun atualizarVotado(filmeId: String?) {
        votadoFilmeId = filmeId
        notifyDataSetChanged()
    }

    /**
     * Quantidade de itens da lista.
     */
    override fun getItemCount(): Int = filmes.size
}
