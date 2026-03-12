package com.example.paycontroller.ui.gastos

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paycontroller.R
import com.example.paycontroller.viewmodel.GastoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MisGastosFragment : Fragment() {

    private lateinit var viewModel: GastoViewModel
    private lateinit var adapter: GastoAdapter
    private var fechaSeleccionada: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_mis_gastos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[GastoViewModel::class.java]

        val tvTotal = view.findViewById<TextView>(R.id.tvTotal)
        val tvVacio = view.findViewById<TextView>(R.id.tvVacio)
        val btnFiltro = view.findViewById<Button>(R.id.btnFiltro)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerGastos)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAgregar)

        // Configurar RecyclerView
        adapter = GastoAdapter { gasto ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar gasto")
                .setMessage("¿Estás seguro de eliminar \"${gasto.titulo}\"?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.eliminarGasto(gasto)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        // Observar categorías (para mostrar nombres y colores)
        viewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            adapter.actualizarCategorias(categorias)
        }

        // Cargar gastos del día
        cargarGastos(tvTotal, tvVacio)

        // Botón filtro
        btnFiltro.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = fechaSeleccionada }
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val nueva = Calendar.getInstance().apply { set(year, month, day) }
                    fechaSeleccionada = nueva.timeInMillis
                    btnFiltro.text = formatoFecha(fechaSeleccionada)
                    cargarGastos(tvTotal, tvVacio)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Formulario nuevo gasto
        fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AgregarGastoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun cargarGastos(tvTotal: TextView, tvVacio: TextView) {
        // Gastos del día
        viewModel.gastosPorDia(fechaSeleccionada).observe(viewLifecycleOwner) { gastos ->
            adapter.actualizarGastos(gastos)
            tvVacio.visibility = if (gastos.isEmpty()) View.VISIBLE else View.GONE
        }

        // Total del día
        val cal = Calendar.getInstance().apply {
            timeInMillis = fechaSeleccionada
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val inicio = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val fin = cal.timeInMillis

        viewModel.totalEnRango(inicio, fin).observe(viewLifecycleOwner) { total ->
            tvTotal.text = String.format("$%.2f MXN", total ?: 0.0)
        }
    }

    private fun formatoFecha(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MMM/yyyy", Locale("es", "MX"))
        return sdf.format(Date(timestamp))
    }
}