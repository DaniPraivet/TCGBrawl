package com.example.tcgbrawl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.tcgbrawl.vista.FragmentoLeyendas
import com.example.tcgbrawl.vista.EstadisticasFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ActividadPrincipal : AppCompatActivity() {

    private lateinit var paginador: ViewPager2
    private lateinit var pestanas: TabLayout

    override fun onCreate(estadoGuardado: Bundle?) {
        super.onCreate(estadoGuardado)
        setContentView(R.layout.activity_main)

        val usuario = intent.getStringExtra("USER_LOGIN") ?: "Brawler"
        supportActionBar?.title = "TCG Brawl"
        supportActionBar?.subtitle = "Jugador: $usuario"

        paginador = findViewById(R.id.viewPager)
        pestanas  = findViewById(R.id.tabLayout)

        paginador.adapter = AdaptadorPaginas(this)

        // vincula las pestañas con el paginador
        TabLayoutMediator(pestanas, paginador) { pestana, posicion ->
            when (posicion) {
                0 -> { pestana.text = "Roster"; pestana.setIcon(R.drawable.ic_cards) }
                1 -> { pestana.text = "Stats";  pestana.setIcon(R.drawable.ic_stats) }
            }
        }.attach()
    }

    private class AdaptadorPaginas(actividad: FragmentActivity) : FragmentStateAdapter(actividad) {
        override fun getItemCount() = 2
        override fun createFragment(posicion: Int): Fragment = when (posicion) {
            0    -> FragmentoLeyendas()
            else -> EstadisticasFragment()
        }
    }
}
