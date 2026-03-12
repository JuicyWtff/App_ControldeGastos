package com.example.paycontroller.ui.gastos

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.paycontroller.R
import com.example.paycontroller.data.entities.Categoria
import com.example.paycontroller.data.entities.Gasto
import com.example.paycontroller.viewmodel.GastoViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AgregarGastoFragment : Fragment() {

    private lateinit var viewModel: GastoViewModel
    private var fechaSeleccionada: Long = System.currentTimeMillis()
    private var categoriasList: List<Categoria> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_agregar_gasto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[GastoViewModel::class.java]

        val etTitulo = view.findViewById<TextInputEditText>(R.id.etTitulo)
        val etDescripcion = view.findViewById<TextInputEditText>(R.id.etDescripcion)
        val etMonto = view.findViewById<TextInputEditText>(R.id.etMonto)
        val btnFecha = view.findViewById<Button>(R.id.btnFecha)
        val spinner = view.findViewById<Spinner>(R.id.spinnerCategoria)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        // Mostrar fecha actual
        actualizarTextoFecha(btnFecha)

        // DatePicker
        btnFecha.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = fechaSeleccionada }
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    fechaSeleccionada = Calendar.getInstance().apply {
                        set(year, month, day)
                    }.timeInMillis
                    actualizarTextoFecha(btnFecha)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Cargar categorías en el Spinner
        viewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            categoriasList = categorias
            val nombres = categorias.map { it.nombre }
            val adapterSpinner = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                nombres
            )
            spinner.adapter = adapterSpinner
        }

        // Guardar
        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim().ifEmpty { null }
            val montoText = etMonto.text.toString().trim()

            // Validaciones
            if (titulo.isEmpty()) {
                etTitulo.error = "Escribe un título"
                return@setOnClickListener
            }
            if (montoText.isEmpty()) {
                etMonto.error = "Escribe el monto"
                return@setOnClickListener
            }
            if (categoriasList.isEmpty()) {
                Toast.makeText(requireContext(), "Primero agrega una categoría", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoText.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                etMonto.error = "Monto inválido"
                return@setOnClickListener
            }

            val categoriaSeleccionada = categoriasList[spinner.selectedItemPosition]

            val gasto = Gasto(
                titulo = titulo,
                descripcion = descripcion,
                monto = monto,
                fecha = fechaSeleccionada,
                categoriaId = categoriaSeleccionada.id
            )

            viewModel.insertarGasto(gasto)
            Toast.makeText(requireContext(), "Gasto guardado", Toast.LENGTH_SHORT).show()

            // Regresar al fragment anterior
            parentFragmentManager.popBackStack()
        }
    }

    private fun actualizarTextoFecha(btn: Button) {
        val sdf = SimpleDateFormat("dd/MMM/yyyy", Locale("es", "MX"))
        btn.text = sdf.format(Date(fechaSeleccionada))
    }
}