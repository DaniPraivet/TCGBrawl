package com.example.tcgbrawl.vista

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tcgbrawl.R

class InfoAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_app)

        // Solo cierra la ventana, no devuelve datos
        findViewById<Button>(R.id.btnAceptarInfo).setOnClickListener {
            finish()
        }
    }
}
