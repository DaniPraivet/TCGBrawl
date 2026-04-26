package com.example.tcgbrawl.vista

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.tcgbrawl.R

class PreferenciasActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var rgTamano: RadioGroup
    private lateinit var rgOrden: RadioGroup
    private lateinit var rgModo: RadioGroup
    private lateinit var btnGuardar: Button

    companion object {
        const val PREFS_NAME = "tcgbrawl_prefs"
        const val KEY_TAMANO = "tamano_texto"
        const val KEY_ORDEN  = "orden_roster"
        const val KEY_MODO   = "modo_interfaz"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferencias)

        prefs      = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        rgTamano   = findViewById(R.id.rgTamanoTexto)
        rgOrden    = findViewById(R.id.rgOrden)
        rgModo     = findViewById(R.id.rgModo)
        btnGuardar = findViewById(R.id.btnGuardarPrefs)

        cargarPreferencias()

        btnGuardar.setOnClickListener {
            guardarPreferencias()
            Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarPreferencias() {
        when (prefs.getString(KEY_TAMANO, "normal")) {
            "pequeno" -> rgTamano.check(R.id.rbPequeno)
            "grande"  -> rgTamano.check(R.id.rbGrande)
            else      -> rgTamano.check(R.id.rbNormal)
        }
        when (prefs.getString(KEY_ORDEN, "none")) {
            "az" -> rgOrden.check(R.id.rbOrdenAZ)
            else -> rgOrden.check(R.id.rbOrdenNinguno)
        }
        when (prefs.getString(KEY_MODO, "oscuro")) {
            "claro" -> rgModo.check(R.id.rbClaro)
            else    -> rgModo.check(R.id.rbOscuro)
        }
    }

    private fun guardarPreferencias() {
        val tamano = when (rgTamano.checkedRadioButtonId) {
            R.id.rbPequeno -> "pequeno"
            R.id.rbGrande  -> "grande"
            else           -> "normal"
        }
        val orden = when (rgOrden.checkedRadioButtonId) {
            R.id.rbOrdenAZ -> "az"
            else           -> "none"
        }
        val modo = when (rgModo.checkedRadioButtonId) {
            R.id.rbClaro -> "claro"
            else         -> "oscuro"
        }

        prefs.edit()
            .putString(KEY_TAMANO, tamano)
            .putString(KEY_ORDEN, orden)
            .putString(KEY_MODO, modo)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (modo == "claro") AppCompatDelegate.MODE_NIGHT_NO
            else AppCompatDelegate.MODE_NIGHT_YES
        )
    }
}