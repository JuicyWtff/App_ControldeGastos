package com.example.paycontroller


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.paycontroller.ui.categorias.CategoriasFragment
import com.example.paycontroller.ui.gastos.MisGastosFragment
import com.example.paycontroller.ui.reportes.ReportesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.ViewModelProvider
import com.example.paycontroller.data.entities.Categoria
import com.example.paycontroller.viewmodel.GastoViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProvider(this)[GastoViewModel::class.java]
        viewModel.categorias.observe(this) { categorias ->
            if (categorias.isEmpty()) {
                viewModel.insertarCategoria(Categoria(nombre = "Transporte", color = "#4CAF50", esDefault = true))
                viewModel.insertarCategoria(Categoria(nombre = "Comida", color = "#FF9800", esDefault = true))
                viewModel.insertarCategoria(Categoria(nombre = "Entretenimiento", color = "#E91E63", esDefault = true))
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Cargar el fragment inicial
        if (savedInstanceState == null) {
            loadFragment(MisGastosFragment())
        }

        // Cambiar fragment en el navigate
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_gastos -> loadFragment(MisGastosFragment())
                R.id.nav_reportes -> loadFragment(ReportesFragment())
                R.id.nav_categorias -> loadFragment(CategoriasFragment())
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}