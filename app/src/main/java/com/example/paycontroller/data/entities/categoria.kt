package com.example.paycontroller.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val color: String = "#4CAF50",   // Color hex por default
    val esDefault: Boolean = false    // Las predeterminadas no se borran
)