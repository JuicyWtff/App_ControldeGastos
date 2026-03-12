package com.example.paycontroller.data.repository

import androidx.lifecycle.LiveData
import com.example.paycontroller.data.database.CategoriaDao
import com.example.paycontroller.data.database.CategoriaTotal
import com.example.paycontroller.data.database.GastoDao
import com.example.paycontroller.data.entities.Categoria
import com.example.paycontroller.data.entities.Gasto

class GastoRepository(
    private val gastoDao: GastoDao,
    private val categoriaDao: CategoriaDao
) {
    //Gastos
    suspend fun insertarGasto(gasto: Gasto) = gastoDao.insertar(gasto)
    suspend fun eliminarGasto(gasto: Gasto) = gastoDao.eliminar(gasto)

    fun gastosPorDia(inicio: Long, fin: Long): LiveData<List<Gasto>> =
        gastoDao.gastosPorDia(inicio, fin)

    fun totalEnRango(desde: Long, hasta: Long): LiveData<Double> =
        gastoDao.totalEnRango(desde, hasta)

    fun gastosPorCategoria(desde: Long, hasta: Long): LiveData<List<CategoriaTotal>> =
        gastoDao.gastosPorCategoria(desde, hasta)

    //Categorías
    suspend fun insertarCategoria(categoria: Categoria) = categoriaDao.insertar(categoria)
    suspend fun eliminarCategoria(categoria: Categoria) = categoriaDao.eliminar(categoria)

    fun obtenerCategorias(): LiveData<List<Categoria>> =
        categoriaDao.obtenerTodas()
}