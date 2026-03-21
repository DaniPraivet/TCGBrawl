package com.example.tcgbrawl.modelo

data class Leyenda(
    val id: Int,
    val nombre: String,
    val arma1: String,
    val arma2: String,
    val fuerza: Int,
    val destreza: Int,
    val defensa: Int,
    val velocidad: Int,
    val imagenUri: String?
)