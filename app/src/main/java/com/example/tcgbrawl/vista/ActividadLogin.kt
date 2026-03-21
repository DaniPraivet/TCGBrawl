package com.example.tcgbrawl.vista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tcgbrawl.ActividadPrincipal
import com.example.tcgbrawl.R
import com.example.tcgbrawl.modelo.GestorBBDD

class ActividadLogin : AppCompatActivity() {

    private lateinit var etLogin: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button
    private lateinit var tvError: TextView
    private lateinit var barraProgreso: ProgressBar
    private lateinit var baseDatos: GestorBBDD

    override fun onCreate(estadoGuardado: Bundle?) {
        super.onCreate(estadoGuardado)
        setContentView(R.layout.activity_login)

        baseDatos     = GestorBBDD(this)
        etLogin       = findViewById(R.id.etLogin)
        etContrasena  = findViewById(R.id.etPassword)
        btnAceptar    = findViewById(R.id.btnAceptar)
        btnCancelar   = findViewById(R.id.btnCancelar)
        tvError       = findViewById(R.id.tvError)
        barraProgreso = findViewById(R.id.progressBar)

        btnAceptar.setOnClickListener {
            val usuario    = etLogin.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                tvError.text = "Por favor, introduce usuario y contraseña"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            barraProgreso.visibility = View.VISIBLE
            btnAceptar.isEnabled = false

            // valida credenciales contra la BD con hash MD5
            val valido = baseDatos.validarUsuario(usuario, contrasena)

            barraProgreso.visibility = View.GONE
            btnAceptar.isEnabled = true

            if (valido) {
                tvError.visibility = View.GONE
                val intencion = Intent(this, ActividadPrincipal::class.java)
                intencion.putExtra("USER_LOGIN", usuario)
                startActivity(intencion)
                finish()
            } else {
                tvError.text = "Usuario o contraseña incorrectos"
                tvError.visibility = View.VISIBLE
                etContrasena.text.clear()
            }
        }

        btnCancelar.setOnClickListener { finish() }
    }
}
