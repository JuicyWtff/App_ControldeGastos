package com.example.paycontroller.ui.categorias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paycontroller.R
import com.example.paycontroller.data.entities.Categoria
import com.example.paycontroller.viewmodel.GastoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriasFragment : Fragment() {

    private lateinit var viewModel: GastoViewModel
    private lateinit var adapter: CategoriaAdapter

    private val colores = listOf(
        "#4CAF50", "#FF9800", "#E91E63",
        "#2196F3", "#9C27B0", "#00BCD4",
        "#FF5722", "#607D8B", "#795548", "#CDDC39"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_categorias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[GastoViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerCategorias)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAgregarCat)

        //Grid 2 columnas
        adapter = CategoriaAdapter { categoria ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar categoría")
                .setMessage("¿Eliminar \"${categoria.nombre}\"? Se borrarán todos los gastos asociados.")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.eliminarCategoria(categoria)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        recycler.adapter = adapter

        viewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            adapter.actualizar(categorias)
        }

        fab.setOnClickListener {
            mostrarDialogoAgregar()
        }
    }

    private fun mostrarDialogoAgregar() {
        val input = EditText(requireContext()).apply {
            hint = "Nombre de la categoría"
            setPadding(48, 32, 48, 32)
        }

        val colorAleatorio = colores.random()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nueva categoría")
            .setView(input)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = input.text.toString().trim()
                if (nombre.isEmpty()) {
                    Toast.makeText(requireContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.insertarCategoria(
                        Categoria(nombre = nombre, color = colorAleatorio)
                    )
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}