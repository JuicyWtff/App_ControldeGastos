package com.example.paycontroller.ui.reportes

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.paycontroller.R
import com.example.paycontroller.data.database.CategoriaTotal
import com.example.paycontroller.viewmodel.GastoViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.*

class ReportesFragment : Fragment() {

    private lateinit var viewModel: GastoViewModel
    private lateinit var pieChart: PieChart
    private lateinit var tvTotal: TextView
    private lateinit var layoutDesglose: LinearLayout
    private lateinit var btnDia: Button
    private lateinit var btnSemana: Button
    private lateinit var btnMes: Button

    private val coloresGrafica = listOf(
        "#4CAF50", "#FF9800", "#E91E63",
        "#2196F3", "#9C27B0", "#00BCD4",
        "#FF5722", "#607D8B", "#795548", "#CDDC39"
    )

    // Para limpiar observers anteriores
    private var totalObserver: LiveData<Double>? = null
    private var categoriaObserver: LiveData<List<CategoriaTotal>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_reportes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[GastoViewModel::class.java]

        pieChart = view.findViewById(R.id.pieChart)
        tvTotal = view.findViewById(R.id.tvTotalReporte)
        layoutDesglose = view.findViewById(R.id.layoutDesglose)
        btnDia = view.findViewById(R.id.btnDia)
        btnSemana = view.findViewById(R.id.btnSemana)
        btnMes = view.findViewById(R.id.btnMes)

        configurarPieChart()

        // Botones de filtro
        btnDia.setOnClickListener { cargarReporte("dia") }
        btnSemana.setOnClickListener { cargarReporte("semana") }
        btnMes.setOnClickListener { cargarReporte("mes") }

        // Cargar mes por default
        cargarReporte("mes")
    }

    private fun configurarPieChart() {
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 50f
        pieChart.transparentCircleRadius = 55f
        pieChart.setDrawEntryLabels(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.legend.isEnabled = false
        pieChart.setNoDataText("Sin datos para este periodo")
    }

    private fun cargarReporte(periodo: String) {
        // Resaltar botón activo
        btnDia.alpha = if (periodo == "dia") 1f else 0.5f
        btnSemana.alpha = if (periodo == "semana") 1f else 0.5f
        btnMes.alpha = if (periodo == "mes") 1f else 0.5f

        val (desde, hasta) = calcularRango(periodo)

        // Remover observers anteriores
        totalObserver?.removeObservers(viewLifecycleOwner)
        categoriaObserver?.removeObservers(viewLifecycleOwner)

        // Total del periodo
        val totalLive = viewModel.totalEnRango(desde, hasta)
        totalObserver = totalLive
        totalLive.observe(viewLifecycleOwner) { total ->
            tvTotal.text = String.format("$%.2f MXN", total ?: 0.0)
        }

        // Gastos por categoría
        val catLive = viewModel.gastosPorCategoria(desde, hasta)
        categoriaObserver = catLive
        catLive.observe(viewLifecycleOwner) { datos ->
            actualizarGrafica(datos)
            actualizarDesglose(datos)
        }
    }

    private fun calcularRango(periodo: String): Pair<Long, Long> {
        val cal = Calendar.getInstance()

        // Fin = ahora
        val hasta = cal.timeInMillis

        // Inicio según periodo
        when (periodo) {
            "dia" -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
            }
            "semana" -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
            "mes" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
        }
        val desde = cal.timeInMillis
        return Pair(desde, hasta)
    }

    private fun actualizarGrafica(datos: List<CategoriaTotal>) {
        if (datos.isEmpty()) {
            pieChart.clear()
            pieChart.invalidate()
            return
        }

        val entries = datos.map { PieEntry(it.total.toFloat(), it.categoria) }
        val dataSet = PieDataSet(entries, "")

        dataSet.colors = datos.mapIndexed { index, _ ->
            Color.parseColor(coloresGrafica[index % coloresGrafica.size])
        }
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        pieChart.data = PieData(dataSet)
        pieChart.animateY(500)
        pieChart.invalidate()
    }

    private fun actualizarDesglose(datos: List<CategoriaTotal>) {
        layoutDesglose.removeAllViews()

        val total = datos.sumOf { it.total }

        datos.forEachIndexed { index, item ->
            val card = CardView(requireContext()).apply {
                radius = 24f
                cardElevation = 4f
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 16) }
                layoutParams = params
            }

            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(32, 24, 32, 24)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            // Circulo de color
            val circle = View(requireContext()).apply {
                val size = 32
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(0, 0, 24, 0)
                }
                background = android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.OVAL
                    setColor(Color.parseColor(coloresGrafica[index % coloresGrafica.size]))
                }
            }

            // Nombre categoría
            val tvNombre = TextView(requireContext()).apply {
                text = item.categoria
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            // Porcentaje
            val porcentaje = if (total > 0) (item.total / total * 100) else 0.0
            val tvPorcentaje = TextView(requireContext()).apply {
                text = String.format("%.0f%%", porcentaje)
                textSize = 13f
                setTextColor(Color.GRAY)
                setPadding(16, 0, 16, 0)
            }

            // Monto
            val tvMonto = TextView(requireContext()).apply {
                text = String.format("$%.2f", item.total)
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            row.addView(circle)
            row.addView(tvNombre)
            row.addView(tvPorcentaje)
            row.addView(tvMonto)
            card.addView(row)
            layoutDesglose.addView(card)
        }
    }
}