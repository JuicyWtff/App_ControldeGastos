package com.example.paycontroller.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.paycontroller.data.entities.Categoria
import com.example.paycontroller.data.entities.Gasto

@Database(
    entities = [Categoria::class, Gasto::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun gastoDao(): GastoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "paycontroller_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}