package com.example.paycontroller.ui.categorias

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paycontroller.R
import com.example.paycontroller.data.entities.Categoria

class CategoriaAdapter(
    private val onEliminar: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    private var categorias: List<Categoria> = emptyList()

    fun actualizar(nuevas: List<Categoria>) {
        categorias = nuevas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]

        holder.tvNombre.text = categoria.nombre

        // Color de categoria
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(categoria.color))
        }
        holder.viewColor.background = drawable

        // Ocultar basura si es default
        if (categoria.esDefault) {
            holder.btnEliminar.visibility = View.GONE
        } else {
            holder.btnEliminar.visibility = View.VISIBLE
            holder.btnEliminar.setOnClickListener { onEliminar(categoria) }
        }
    }

    override fun getItemCount() = categorias.size

    class CategoriaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreCat)
        val viewColor: View = view.findViewById(R.id.viewColorCat)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarCat)
    }
}