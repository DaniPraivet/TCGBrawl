package com.example.tcgbrawl.controlador

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcgbrawl.R
import com.example.tcgbrawl.modelo.Leyenda

class AdaptadorLeyenda(
    private val leyendas: MutableList<Leyenda>,
    private val alEliminar: (Leyenda) -> Unit
) : RecyclerView.Adapter<AdaptadorLeyenda.VistaLeyenda>() {

    inner class VistaLeyenda(vista: View) : RecyclerView.ViewHolder(vista) {
        val ivImagen: ImageView   = vista.findViewById(R.id.ivLegendImage)
        val tvNombre: TextView    = vista.findViewById(R.id.tvLegendName)
        val tvArmas: TextView     = vista.findViewById(R.id.tvLegendWeapons)
        val tvFuerza: TextView    = vista.findViewById(R.id.tvStatFuerza)
        val tvDestreza: TextView  = vista.findViewById(R.id.tvStatDestreza)
        val tvDefensa: TextView   = vista.findViewById(R.id.tvStatDefensa)
        val tvVelocidad: TextView = vista.findViewById(R.id.tvStatVelocidad)
        val ivEliminar: ImageView = vista.findViewById(R.id.ivDeleteLegend)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoVista: Int): VistaLeyenda {
        val vista = LayoutInflater.from(padre.context)
            .inflate(R.layout.item_legend, padre, false)
        return VistaLeyenda(vista)
    }

    override fun onBindViewHolder(titular: VistaLeyenda, posicion: Int) {
        val leyenda = leyendas[posicion]

        titular.tvNombre.text    = leyenda.nombre
        titular.tvArmas.text     = "${leyenda.arma1}  •  ${leyenda.arma2}"
        titular.tvFuerza.text    = "💪 ${leyenda.fuerza}"
        titular.tvDestreza.text  = "🤺 ${leyenda.destreza}"
        titular.tvDefensa.text   = "🛡️ ${leyenda.defensa}"
        titular.tvVelocidad.text = "💨 ${leyenda.velocidad}"

        // carga la imagen o usa la imagen por defecto
        if (!leyenda.imagenUri.isNullOrEmpty()) {
            try {
                titular.ivImagen.setImageURI(Uri.parse(leyenda.imagenUri))
            } catch (e: Exception) {
                titular.ivImagen.setImageResource(R.drawable.foto)
            }
        } else {
            titular.ivImagen.setImageResource(R.drawable.foto)
        }

        titular.ivEliminar.setOnClickListener { alEliminar(leyenda) }
    }

    override fun getItemCount(): Int = leyendas.size
}
