package com.example.paycontroller.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.paycontroller.data.entities.Gasto

// Clase auxiliar para reportes por categoría
data class CategoriaTotal(
    val categoria: String,
    val total: Double
)

@Dao
interface GastoDao {

    @Insert
    suspend fun insertar(gasto: Gasto)

    @Delete
    suspend fun eliminar(gasto: Gasto)

    // Gastos de un día específico
    @Query("""
        SELECT * FROM gastos 
        WHERE fecha BETWEEN :inicioDelDia AND :finDelDia 
        ORDER BY fecha DESC
    """)
    fun gastosPorDia(inicioDelDia: Long, finDelDia: Long): LiveData<List<Gasto>>

    // Total gastado en un rango de fechas
    @Query("""
        SELECT COALESCE(SUM(monto), 0.0) FROM gastos 
        WHERE fecha BETWEEN :desde AND :hasta
    """)
    fun totalEnRango(desde: Long, hasta: Long): LiveData<Double>

    // Gastos agrupados por categoría (para reportes)
    @Query("""
        SELECT c.nombre AS categoria, SUM(g.monto) AS total
        FROM gastos g INNER JOIN categorias c ON g.categoriaId = c.id
        WHERE g.fecha BETWEEN :desde AND :hasta
        GROUP BY g.categoriaId
    """)
    fun gastosPorCategoria(desde: Long, hasta: Long): LiveData<List<CategoriaTotal>>
}