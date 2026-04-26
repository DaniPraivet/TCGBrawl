package com.example.tcgbrawl

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.tcgbrawl.vista.FragmentoLeyendas
import com.example.tcgbrawl.vista.EstadisticasFragment
import com.example.tcgbrawl.vista.InfoAppActivity
import com.example.tcgbrawl.vista.PreferenciasActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.jvm.java

class ActividadPrincipal : AppCompatActivity() {

    private lateinit var paginador: ViewPager2
    private lateinit var pestanas: TabLayout
    private lateinit var toolbar: Toolbar
    private var usuarioLogin: String = "Brawler"

    override fun onCreate(estadoGuardado: Bundle?) {
        super.onCreate(estadoGuardado)
        setContentView(R.layout.activity_main)

        usuarioLogin = intent.getStringExtra("USER_LOGIN") ?: "Brawler"

        // Configurar Toolbar como ActionBar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "TCG Brawl"
        supportActionBar?.subtitle = "👤 $usuarioLogin"   // (c) login del usuario autenticado

        paginador = findViewById(R.id.viewPager)
        pestanas  = findViewById(R.id.tabLayout)

        paginador.adapter = AdaptadorPaginas(this)

        TabLayoutMediator(pestanas, paginador) { pestana, posicion ->
            when (posicion) {
                0 -> { pestana.text = "Roster"; pestana.setIcon(R.drawable.ic_cards) }
                1 -> { pestana.text = "Stats";  pestana.setIcon(R.drawable.ic_stats) }
            }
        }.attach()
    }

    // (b) Infla el menú con icono información + overflow preferencias
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    // (b)(d) Gestiona pulsaciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                startActivity(Intent(this, InfoAppActivity::class.java))
                true
            }
            R.id.action_preferencias -> {
                startActivity(Intent(this, PreferenciasActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class AdaptadorPaginas(actividad: FragmentActivity) : FragmentStateAdapter(actividad) {
        override fun getItemCount() = 2
        override fun createFragment(posicion: Int): Fragment = when (posicion) {
            0    -> FragmentoLeyendas()
            else -> EstadisticasFragment()
        }
    }
}