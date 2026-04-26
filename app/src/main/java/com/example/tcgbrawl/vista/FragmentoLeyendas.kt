package com.example.tcgbrawl.vista

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcgbrawl.R
import com.example.tcgbrawl.controlador.AdaptadorLeyenda
import com.example.tcgbrawl.modelo.GestorBBDD
import com.example.tcgbrawl.modelo.Leyenda
import com.example.tcgbrawl.vista.PreferenciasActivity.Companion.KEY_ORDEN
import com.example.tcgbrawl.vista.PreferenciasActivity.Companion.KEY_TAMANO
import com.example.tcgbrawl.vista.PreferenciasActivity.Companion.PREFS_NAME
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class FragmentoLeyendas : Fragment() {

    private lateinit var listaRecycler: RecyclerView
    private lateinit var botonFlotante: FloatingActionButton
    private lateinit var tvListaVacia: TextView
    private lateinit var baseDatos: GestorBBDD
    private lateinit var adaptador: AdaptadorLeyenda
    private val leyendas = mutableListOf<Leyenda>()

    private val lanzadorAgregarLeyenda =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                refrescar()
                Snackbar.make(requireView(), "¡Leyenda añadida al roster!", Snackbar.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_legends, container, false)

    override fun onViewCreated(vista: View, savedInstanceState: Bundle?) {
        super.onViewCreated(vista, savedInstanceState)

        baseDatos     = GestorBBDD(requireContext())
        listaRecycler = vista.findViewById(R.id.recyclerViewLegends)
        botonFlotante = vista.findViewById(R.id.fabAddLegend)
        tvListaVacia  = vista.findViewById(R.id.tvEmptyList)

        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val tamano = prefs.getString(KEY_TAMANO, "normal") ?: "normal"
        val orden  = prefs.getString(KEY_ORDEN, "none")

        leyendas.addAll(baseDatos.obtenerTodasLeyendas())
        if (orden == "az") leyendas.sortBy { it.nombre.lowercase() }

        adaptador = AdaptadorLeyenda(leyendas, { mostrarDialogoEliminar(it) }, tamano)
        listaRecycler.layoutManager = LinearLayoutManager(requireContext())
        listaRecycler.adapter = adaptador

        actualizarVacio()

        botonFlotante.setOnClickListener {
            lanzadorAgregarLeyenda.launch(Intent(requireContext(), AgregarLeyendaActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        aplicarPreferencias()
        refrescar()
    }

    private fun aplicarPreferencias() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        adaptador.tamanoTexto = prefs.getString(KEY_TAMANO, "normal") ?: "normal"

        val orden = prefs.getString(KEY_ORDEN, "none")
        if (orden == "az") {
            leyendas.sortBy { it.nombre.lowercase() }
        }
        adaptador.notifyDataSetChanged()
    }

    fun refrescar() {
        if (!isAdded) return
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val orden = prefs.getString(KEY_ORDEN, "none")
        leyendas.clear()
        leyendas.addAll(baseDatos.obtenerTodasLeyendas())
        if (orden == "az") leyendas.sortBy { it.nombre.lowercase() }
        adaptador.notifyDataSetChanged()
        actualizarVacio()
        // también refresca el fragmento de estadísticas
        (parentFragmentManager.findFragmentByTag("f1") as? EstadisticasFragment)?.refrescar()
    }

    private fun actualizarVacio() {
        listaRecycler.visibility = if (leyendas.isEmpty()) View.GONE else View.VISIBLE
        tvListaVacia.visibility  = if (leyendas.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun mostrarDialogoEliminar(leyenda: Leyenda) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar leyenda")
            .setMessage("¿Quieres retirar a \"${leyenda.nombre}\" de tu roster?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Eliminar") { _, _ ->
                baseDatos.eliminarLeyenda(leyenda.id)
                refrescar()
                // opción de deshacer la eliminación
                Snackbar.make(requireView(), "\"${leyenda.nombre}\" retirado", Snackbar.LENGTH_LONG)
                    .setAction("Deshacer") {
                        baseDatos.insertarLeyenda(leyenda)
                        refrescar()
                    }.show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
