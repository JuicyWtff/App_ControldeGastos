package com.example.paycontroller.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.paycontroller.data.database.AppDatabase
import com.example.paycontroller.data.database.CategoriaTotal
import com.example.paycontroller.data.entities.Categoria
import com.example.paycontroller.data.entities.Gasto
import com.example.paycontroller.data.repository.GastoRepository
import kotlinx.coroutines.launch
import java.util.*

class GastoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GastoRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GastoRepository(db.gastoDao(), db.categoriaDao())
    }

    // ── Categorías ──
    val categorias: LiveData<List<Categoria>> = repository.obtenerCategorias()

    fun insertarCategoria(categoria: Categoria) = viewModelScope.launch {
        repository.insertarCategoria(categoria)
    }

    fun eliminarCategoria(categoria: Categoria) = viewModelScope.launch {
        repository.eliminarCategoria(categoria)
    }

    // ── Gastos ──
    fun insertarGasto(gasto: Gasto) = viewModelScope.launch {
        repository.insertarGasto(gasto)
    }

    fun eliminarGasto(gasto: Gasto) = viewModelScope.launch {
        repository.eliminarGasto(gasto)
    }

    fun gastosPorDia(fecha: Long): LiveData<List<Gasto>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = fecha
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val inicio = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val fin = calendar.timeInMillis
        return repository.gastosPorDia(inicio, fin)
    }

    // ── Reportes ──
    fun totalEnRango(desde: Long, hasta: Long): LiveData<Double> =
        repository.totalEnRango(desde, hasta)

    fun gastosPorCategoria(desde: Long, hasta: Long): LiveData<List<CategoriaTotal>> =
        repository.gastosPorCategoria(desde, hasta)
}