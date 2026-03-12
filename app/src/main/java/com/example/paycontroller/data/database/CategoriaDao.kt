package com.example.paycontroller.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.paycontroller.data.entities.Categoria

@Dao
interface CategoriaDao {

    @Insert
    suspend fun insertar(categoria: Categoria)

    @Delete
    suspend fun eliminar(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerTodas(): LiveData<List<Categoria>>
}