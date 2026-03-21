package com.example.tcgbrawl.vista

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tcgbrawl.R
import com.example.tcgbrawl.modelo.GestorBBDD
import com.example.tcgbrawl.modelo.Leyenda
import com.google.android.material.snackbar.Snackbar

class AgregarLeyendaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spinnerArma1: Spinner
    private lateinit var spinnerArma2: Spinner
    private lateinit var sbFuerza: SeekBar
    private lateinit var sbDestreza: SeekBar
    private lateinit var sbDefensa: SeekBar
    private lateinit var sbVelocidad: SeekBar
    private lateinit var tvFuerzaVal: TextView
    private lateinit var tvDestrezaVal: TextView
    private lateinit var tvDefensaVal: TextView
    private lateinit var tvVelocidadVal: TextView
    private lateinit var ivPreview: ImageView
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var tvImagenSeleccionada: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private lateinit var baseDatos: GestorBBDD
    private var uriImagenSeleccionada: Uri? = null

    private val ARMAS = arrayOf(
        "Gran Espada", "Espada", "Martillo", "Hacha", "Lanza",
        "Pistolas", "Arco", "Katars", "Guantes", "Hoz",
        "Cañón", "Lanza de Batalla", "Cadena", "Botas"
    )

    // selector moderno de imágenes (Android 13+)
    private val selectorMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { aplicarImagen(it) }
        }

    // selector alternativo para versiones anteriores
    private val selectorAlternativo =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { aplicarImagen(it) }
        }

    private val solicitarPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            if (concedido) abrirSelector()
            else Snackbar.make(btnSeleccionarImagen, "Permiso denegado", Snackbar.LENGTH_SHORT).show()
        }

    override fun onCreate(estadoGuardado: Bundle?) {
        super.onCreate(estadoGuardado)
        setContentView(R.layout.activity_add_legend)

        supportActionBar?.title = "Nueva Leyenda"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        baseDatos            = GestorBBDD(this)
        etNombre             = findViewById(R.id.etNombre)
        spinnerArma1         = findViewById(R.id.spinnerArma1)
        spinnerArma2         = findViewById(R.id.spinnerArma2)
        sbFuerza             = findViewById(R.id.sbFuerza)
        sbDestreza           = findViewById(R.id.sbDestreza)
        sbDefensa            = findViewById(R.id.sbDefensa)
        sbVelocidad          = findViewById(R.id.sbVelocidad)
        tvFuerzaVal          = findViewById(R.id.tvFuerzaVal)
        tvDestrezaVal        = findViewById(R.id.tvDestrezaVal)
        tvDefensaVal         = findViewById(R.id.tvDefensaVal)
        tvVelocidadVal       = findViewById(R.id.tvVelocidadVal)
        ivPreview            = findViewById(R.id.ivLegendImagePreview)
        btnSeleccionarImagen = findViewById(R.id.btnSelectImage)
        tvImagenSeleccionada = findViewById(R.id.tvImageSelected)
        btnGuardar           = findViewById(R.id.btnGuardar)
        btnCancelar          = findViewById(R.id.btnCancelar)

        val adaptador1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ARMAS)
        adaptador1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerArma1.adapter = adaptador1

        val adaptador2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ARMAS)
        adaptador2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerArma2.adapter = adaptador2
        spinnerArma2.setSelection(1)

        configurarSeekBar(sbFuerza,    tvFuerzaVal,    "💪")
        configurarSeekBar(sbDestreza,  tvDestrezaVal,  "🎯")
        configurarSeekBar(sbDefensa,   tvDefensaVal,   "🛡️")
        configurarSeekBar(sbVelocidad, tvVelocidadVal, "💨")

        btnSeleccionarImagen.setOnClickListener { comprobarYSeleccionar() }
        btnGuardar.setOnClickListener           { guardar() }
        btnCancelar.setOnClickListener          { setResult(RESULT_CANCELED); finish() }
    }

    private fun configurarSeekBar(barra: SeekBar, etiqueta: TextView, emoji: String) {
        barra.max      = 9
        barra.progress = 4
        etiqueta.text  = "$emoji 5"
        barra.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(b: SeekBar, progreso: Int, delUsuario: Boolean) {
                etiqueta.text = "$emoji ${progreso + 1}"
            }
            override fun onStartTrackingTouch(b: SeekBar) {}
            override fun onStopTrackingTouch(b: SeekBar) {}
        })
    }

    private fun aplicarImagen(uri: Uri) {
        // persiste el permiso de lectura de la URI entre reinicios
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) {}
        uriImagenSeleccionada        = uri
        ivPreview.setImageURI(uri)
        ivPreview.visibility         = View.VISIBLE
        tvImagenSeleccionada.text    = "Imagen seleccionada"
        tvImagenSeleccionada.visibility = View.VISIBLE
    }

    private fun comprobarYSeleccionar() {
        // en Android 13+ no hace falta pedir permiso de almacenamiento
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            abrirSelector()
        } else {
            val permiso = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED)
                abrirSelector()
            else
                solicitarPermiso.launch(permiso)
        }
    }

    private fun abrirSelector() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            selectorMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        else
            selectorAlternativo.launch("image/*")
    }

    private fun guardar() {
        val nombre = etNombre.text.toString().trim()
        if (nombre.isEmpty()) {
            etNombre.error = "El nombre es obligatorio"
            etNombre.requestFocus()
            return
        }
        val arma1 = spinnerArma1.selectedItem.toString()
        val arma2 = spinnerArma2.selectedItem.toString()
        if (arma1 == arma2) {
            Snackbar.make(btnGuardar, "Las dos armas deben ser distintas", Snackbar.LENGTH_SHORT).show()
            return
        }

        val leyenda = Leyenda(
            id        = 0,
            nombre    = nombre,
            arma1     = arma1,
            arma2     = arma2,
            fuerza    = sbFuerza.progress + 1,
            destreza  = sbDestreza.progress + 1,
            defensa   = sbDefensa.progress + 1,
            velocidad = sbVelocidad.progress + 1,
            imagenUri = uriImagenSeleccionada?.toString()
        )

        if (baseDatos.insertarLeyenda(leyenda) > 0) {
            setResult(RESULT_OK)
            finish()
        } else {
            Snackbar.make(btnGuardar, "Error al guardar", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(RESULT_CANCELED); finish(); return true
    }
}
