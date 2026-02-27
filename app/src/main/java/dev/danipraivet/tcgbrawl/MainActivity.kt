package dev.danipraivet.tcgbrawl

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recuperamos el usuario logueado para mostrarlo
        val usuario = intent.getStringExtra("usuario") ?: "Invitado"
        findViewById<TextView>(R.id.tvBienvenida).text = "¡Bienvenido, $usuario!"

        // TODO: Aquí irá el ViewPager2 + TabLayout con las pestañas (Punto 2)
    }
}
