package com.ufpr.oscar_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.ufpr.oscar_app.R
import com.ufpr.oscar_app.model.Filme

/**
 * Adapter da lista de filmes. Funciona com qualquer quantidade de itens (2, 5 ou N)
 * retornada pelo servidor. O clique em um item devolve o filme correspondente
 * via [onFilmeClick], para a Activity abrir os detalhes daquele filme.
 */
class FilmeAdapter(
    private val filmes: List<Filme>,
    private val onFilmeClick: (Filme) -> Unit
) : RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder>() {

    class FilmeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.posterImageView)
        val nome: TextView = itemView.findViewById(R.id.nomeTextView)
        val genero: TextView = itemView.findViewById(R.id.generoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return FilmeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmeViewHolder, position: Int) {
        val filme = filmes[position]

        holder.nome.text = filme.nome
        holder.genero.text = filme.genero

        Picasso.get()
            .load(filme.foto)
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.placeholder_poster)
            .into(holder.poster)

        holder.itemView.setOnClickListener { onFilmeClick(filme) }
    }

    override fun getItemCount(): Int = filmes.size
}
