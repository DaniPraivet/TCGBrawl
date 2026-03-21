package com.example.tcgbrawl.modelo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class GestorBBDD(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        const val NOMBRE_BD  = "TCGBrawlDB"
        const val VERSION_BD = 1

        const val TABLA_USUARIOS   = "usuarios"
        const val COL_USR_ID       = "id"
        const val COL_USR_LOGIN    = "login"
        const val COL_USR_PASS     = "password"

        const val TABLA_LEYENDAS    = "leyendas"
        const val COL_LEY_ID        = "id"
        const val COL_LEY_NOMBRE    = "nombre"
        const val COL_LEY_ARMA1     = "arma1"
        const val COL_LEY_ARMA2     = "arma2"
        const val COL_LEY_FUERZA    = "fuerza"
        const val COL_LEY_DESTREZA  = "destreza"
        const val COL_LEY_DEFENSA   = "defensa"
        const val COL_LEY_VELOCIDAD = "velocidad"
        const val COL_LEY_IMG_URI   = "imagen_uri"
    }

    override fun onCreate(bd: SQLiteDatabase) {
        bd.execSQL(
            """
            CREATE TABLE $TABLA_USUARIOS (
                $COL_USR_ID    INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USR_LOGIN TEXT NOT NULL UNIQUE,
                $COL_USR_PASS  TEXT NOT NULL
            )
        """.trimIndent()
        )

        bd.execSQL(
            """
            CREATE TABLE $TABLA_LEYENDAS (
                $COL_LEY_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LEY_NOMBRE    TEXT NOT NULL,
                $COL_LEY_ARMA1     TEXT NOT NULL,
                $COL_LEY_ARMA2     TEXT NOT NULL,
                $COL_LEY_FUERZA    INTEGER NOT NULL DEFAULT 5,
                $COL_LEY_DESTREZA  INTEGER NOT NULL DEFAULT 5,
                $COL_LEY_DEFENSA   INTEGER NOT NULL DEFAULT 5,
                $COL_LEY_VELOCIDAD INTEGER NOT NULL DEFAULT 5,
                $COL_LEY_IMG_URI   TEXT
            )
        """.trimIndent()
        )

        insertarUsuariosPorDefecto(bd)
        insertarLeyendasPorDefecto(bd)
    }

    override fun onUpgrade(bd: SQLiteDatabase, versionAntigua: Int, versionNueva: Int) {
        bd.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        bd.execSQL("DROP TABLE IF EXISTS $TABLA_LEYENDAS")
        onCreate(bd)
    }

    // convierte la cadena a hash MD5
    fun md5(entrada: String): String {
        val resumen = MessageDigest.getInstance("MD5").digest(entrada.toByteArray())
        return resumen.joinToString("") { "%02x".format(it) }
    }

    private fun insertarUsuariosPorDefecto(bd: SQLiteDatabase) {
        listOf("admin" to "admin123", "danipraivet" to "hola", "jugador" to "tcg1234")
            .forEach { (login, contrasena) ->
                bd.insert(TABLA_USUARIOS, null, ContentValues().apply {
                    put(COL_USR_LOGIN, login)
                    put(COL_USR_PASS, md5(contrasena))
                })
            }
    }

    // comprueba login y contraseña contra la BD
    fun validarUsuario(login: String, contrasena: String): Boolean {
        val cursor = readableDatabase.query(
            TABLA_USUARIOS, arrayOf(COL_USR_ID),
            "$COL_USR_LOGIN = ? AND $COL_USR_PASS = ?",
            arrayOf(login, md5(contrasena)),
            null, null, null
        )
        return cursor.use { it.count > 0 }
    }

    private fun insertarLeyendasPorDefecto(bd: SQLiteDatabase) {
        val predeterminadas = listOf(
            Leyenda(0, "Bodvar",      "Gran Espada", "Martillo", 7, 5, 5, 7, null),
            Leyenda(0, "Cassidy",    "Martillo",    "Pistolas", 6, 5, 7, 6, null),
            Leyenda(0, "Orion",      "Lanza",       "Pistolas", 6, 6, 6, 6, null),
            Leyenda(0, "Lord Vraxx", "Lanza",       "Pistolas", 5, 7, 5, 7, null),
            Leyenda(0, "Gnash",      "Martillo",    "Lanza",    6, 5, 7, 6, null),
            Leyenda(0, "Queen Nai",  "Lanza",       "Katars",   7, 6, 5, 6, null),
            Leyenda(0, "Jhala",      "Hacha",       "Espada",   7, 5, 6, 6, null),
            Leyenda(0, "Kor",        "Gran Espada", "Guantes",  8, 4, 7, 5, null),
        )
        predeterminadas.forEach { insertarLeyenda(it, bd) }
    }

    private fun valoresLeyenda(leyenda: Leyenda) = ContentValues().apply {
        put(COL_LEY_NOMBRE,    leyenda.nombre)
        put(COL_LEY_ARMA1,     leyenda.arma1)
        put(COL_LEY_ARMA2,     leyenda.arma2)
        put(COL_LEY_FUERZA,    leyenda.fuerza)
        put(COL_LEY_DESTREZA,  leyenda.destreza)
        put(COL_LEY_DEFENSA,   leyenda.defensa)
        put(COL_LEY_VELOCIDAD, leyenda.velocidad)
        put(COL_LEY_IMG_URI,   leyenda.imagenUri)
    }

    private fun insertarLeyenda(leyenda: Leyenda, bd: SQLiteDatabase): Long =
        bd.insert(TABLA_LEYENDAS, null, valoresLeyenda(leyenda))

    fun insertarLeyenda(leyenda: Leyenda): Long =
        writableDatabase.insert(TABLA_LEYENDAS, null, valoresLeyenda(leyenda))

    fun obtenerTodasLeyendas(): MutableList<Leyenda> {
        val lista = mutableListOf<Leyenda>()
        val cursor = readableDatabase.query(
            TABLA_LEYENDAS, null, null, null, null, null, "$COL_LEY_NOMBRE ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Leyenda(
                        id        = it.getInt(it.getColumnIndexOrThrow(COL_LEY_ID)),
                        nombre    = it.getString(it.getColumnIndexOrThrow(COL_LEY_NOMBRE)),
                        arma1     = it.getString(it.getColumnIndexOrThrow(COL_LEY_ARMA1)),
                        arma2     = it.getString(it.getColumnIndexOrThrow(COL_LEY_ARMA2)),
                        fuerza    = it.getInt(it.getColumnIndexOrThrow(COL_LEY_FUERZA)),
                        destreza  = it.getInt(it.getColumnIndexOrThrow(COL_LEY_DESTREZA)),
                        defensa   = it.getInt(it.getColumnIndexOrThrow(COL_LEY_DEFENSA)),
                        velocidad = it.getInt(it.getColumnIndexOrThrow(COL_LEY_VELOCIDAD)),
                        imagenUri = it.getString(it.getColumnIndexOrThrow(COL_LEY_IMG_URI))
                    )
                )
            }
        }
        return lista
    }

    fun eliminarLeyenda(id: Int): Int =
        writableDatabase.delete(TABLA_LEYENDAS, "$COL_LEY_ID = ?", arrayOf(id.toString()))

    fun obtenerConteoLeyendas(): Int {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLA_LEYENDAS", null)
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    fun obtenerTopPorFuerza(): String {
        val cursor = readableDatabase.rawQuery(
            "SELECT $COL_LEY_NOMBRE, MAX($COL_LEY_FUERZA) FROM $TABLA_LEYENDAS", null
        )
        return cursor.use { if (it.moveToFirst()) it.getString(0) ?: "—" else "—" }
    }

    fun obtenerTopPorVelocidad(): String {
        val cursor = readableDatabase.rawQuery(
            "SELECT $COL_LEY_NOMBRE, MAX($COL_LEY_VELOCIDAD) FROM $TABLA_LEYENDAS", null
        )
        return cursor.use { if (it.moveToFirst()) it.getString(0) ?: "—" else "—" }
    }

    fun obtenerUsoArmas(): Map<String, Int> {
        val resultado = mutableMapOf<String, Int>()
        // une arma1 y arma2 de todas las leyendas en una sola columna
        val cursor = readableDatabase.rawQuery(
            "SELECT $COL_LEY_ARMA1 as arma FROM $TABLA_LEYENDAS " +
                    "UNION ALL SELECT $COL_LEY_ARMA2 FROM $TABLA_LEYENDAS", null
        )
        cursor.use {
            while (it.moveToNext()) {
                val arma = it.getString(0)
                resultado[arma] = (resultado[arma] ?: 0) + 1
            }
        }
        return resultado.entries.sortedByDescending { it.value }.take(6).associate { it.toPair() }
    }

    fun obtenerEstadisticasPromedio(): Map<String, Double> {
        val cursor = readableDatabase.rawQuery(
            "SELECT AVG($COL_LEY_FUERZA), AVG($COL_LEY_DESTREZA), " +
                    "AVG($COL_LEY_DEFENSA), AVG($COL_LEY_VELOCIDAD) FROM $TABLA_LEYENDAS", null
        )
        return cursor.use {
            if (it.moveToFirst()) mapOf(
                "Fuerza"    to it.getDouble(0),
                "Destreza"  to it.getDouble(1),
                "Defensa"   to it.getDouble(2),
                "Velocidad" to it.getDouble(3)
            ) else emptyMap()
        }
    }
}
