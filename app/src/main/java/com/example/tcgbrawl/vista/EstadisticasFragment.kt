package com.example.tcgbrawl.vista

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tcgbrawl.R
import com.example.tcgbrawl.modelo.GestorBBDD

class EstadisticasFragment : Fragment() {

    private lateinit var baseDatos: GestorBBDD
    private lateinit var tvTotal: TextView
    private lateinit var tvTopFuerza: TextView
    private lateinit var tvTopVelocidad: TextView
    private lateinit var llArmas: LinearLayout
    private lateinit var llEstadisticasPromedio: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_stats, container, false)

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)
        baseDatos              = GestorBBDD(requireContext())
        tvTotal                = vista.findViewById(R.id.tvTotalCards)
        tvTopFuerza            = vista.findViewById(R.id.tvTopFuerza)
        tvTopVelocidad         = vista.findViewById(R.id.tvTopVelocidad)
        llArmas                = vista.findViewById(R.id.llTypeStats)
        llEstadisticasPromedio = vista.findViewById(R.id.llAvgStats)
        refrescar()
    }

    override fun onResume() { super.onResume(); refrescar() }

    fun refrescar() {
        if (!isAdded) return
        tvTotal.text        = baseDatos.obtenerConteoLeyendas().toString()
        tvTopFuerza.text    = baseDatos.obtenerTopPorFuerza()
        tvTopVelocidad.text = baseDatos.obtenerTopPorVelocidad()
        construirBarrasArmas()
        construirPromedioEstats()
    }

    private fun construirBarrasArmas() {
        llArmas.removeAllViews()
        val armas       = baseDatos.obtenerUsoArmas()
        val maxConteo   = armas.values.maxOrNull() ?: 1
        val coloresArmas = listOf("#E94560", "#F5A623", "#7ED321", "#4A90D9", "#9B59B6", "#1ABC9C")

        armas.entries.forEachIndexed { indice, (arma, conteo) ->
            val fila = layoutInflater.inflate(R.layout.item_stat_row, llArmas, false)
            fila.findViewById<TextView>(R.id.tvStatType).apply {
                text = arma
                setTextColor(Color.parseColor(coloresArmas[indice % coloresArmas.size]))
            }
            fila.findViewById<TextView>(R.id.tvStatCount).text = conteo.toString()
            fila.findViewById<ProgressBar>(R.id.pbStatBar).apply {
                max = maxConteo; progress = conteo
            }
            llArmas.addView(fila)
        }
    }

    private fun construirPromedioEstats() {
        llEstadisticasPromedio.removeAllViews()
        val promedios = baseDatos.obtenerEstadisticasPromedio()
        val iconos = mapOf("Fuerza" to "💪", "Destreza" to "🤺", "Defensa" to "🛡️", "Velocidad" to "💨")
        val colores = mapOf(
            "Fuerza"    to "#E94560",
            "Destreza"  to "#F5A623",
            "Defensa"   to "#4A90D9",
            "Velocidad" to "#7ED321"
        )

        promedios.forEach { (estadistica, valor) ->
            val fila = layoutInflater.inflate(R.layout.item_stat_row, llEstadisticasPromedio, false)
            fila.findViewById<TextView>(R.id.tvStatType).apply {
                text = "${iconos[estadistica]} $estadistica"
                setTextColor(Color.parseColor(colores[estadistica] ?: "#FFFFFF"))
            }
            fila.findViewById<TextView>(R.id.tvStatCount).text = String.format("%.1f", valor)
            fila.findViewById<ProgressBar>(R.id.pbStatBar).apply {
                max = 10; progress = valor.toInt()
            }
            llEstadisticasPromedio.addView(fila)
        }
    }
}
