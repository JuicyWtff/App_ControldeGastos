package com.example.paycontroller.ui.gastos

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
import com.example.paycontroller.data.entities.Gasto
import java.text.SimpleDateFormat
import java.util.*


class GastoAdapter(
    private val onEliminar: (Gasto) -> Unit
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    private var gastos: List<Gasto> = emptyList()
    private var categorias: Map<Int, Categoria> = emptyMap()

    fun actualizarGastos(nuevosGastos: List<Gasto>) {
        gastos = nuevosGastos
        notifyDataSetChanged()
    }

    fun actualizarCategorias(lista: List<Categoria>) {
        categorias = lista.associateBy { it.id }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = gastos[position]
        val categoria = categorias[gasto.categoriaId]
        val dateFormat = SimpleDateFormat("dd/MMM/yyyy", Locale("es", "MX"))

        holder.tvTitulo.text = gasto.titulo
        holder.tvMonto.text = String.format("$%.2f", gasto.monto)
        holder.tvCategoria.text = "${categoria?.nombre ?: "Sin categoría"} · ${dateFormat.format(Date(gasto.fecha))}"

        // Color de categoria
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(categoria?.color ?: "#4CAF50"))
            setSize(24, 24)
        }
        holder.viewColor.background = drawable

        // Descripción expandible
        if (!gasto.descripcion.isNullOrEmpty()) {
            holder.tvDescripcion.text = gasto.descripcion

            holder.itemView.setOnClickListener {
                val visible = holder.tvDescripcion.visibility == View.VISIBLE
                holder.tvDescripcion.visibility = if (visible) View.GONE else View.VISIBLE
            }
        } else {
            holder.tvDescripcion.visibility = View.GONE
            holder.itemView.setOnClickListener(null)
        }

        holder.btnEliminar.setOnClickListener { onEliminar(gasto) }
    }

    override fun getItemCount() = gastos.size

    class GastoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvMonto: TextView = view.findViewById(R.id.tvMonto)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val viewColor: View = view.findViewById(R.id.viewColor)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }
}