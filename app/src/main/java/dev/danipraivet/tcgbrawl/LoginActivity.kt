package dev.danipraivet.tcgbrawl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.danipraivet.tcgbrawl.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etLogin: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        etLogin    = findViewById(R.id.etLogin)
        etPassword = findViewById(R.id.etPassword)
        btnAceptar  = findViewById(R.id.btnAceptar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // ── Botón ACEPTAR ────────────────────────────────────────────────────
        btnAceptar.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val clave = etPassword.text.toString().trim()

            when {
                login.isEmpty() -> {
                    etLogin.error = "Introduce tu usuario"
                    etLogin.requestFocus()
                }
                clave.isEmpty() -> {
                    etPassword.error = "Introduce tu contraseña"
                    etPassword.requestFocus()
                }
                else -> {
                    if (dbHelper.validarUsuario(login, clave)) {
                        // Credenciales correctas → pantalla principal
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("usuario", login)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                        etPassword.text?.clear()
                        etPassword.requestFocus()
                    }
                }
            }
        }

        // ── Botón CANCELAR ───────────────────────────────────────────────────
        btnCancelar.setOnClickListener {
            finishAffinity()
        }
    }
}