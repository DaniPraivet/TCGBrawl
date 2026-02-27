package dev.danipraivet.BBDD

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "TCGBrawl"
        const val DATABASE_VERSION = 1

        // Tabla usuarios
        const val TABLE_USUARIOS = "usuarios"
        const val COL_USER_ID = "id"
        const val COL_USER_LOGIN = "login"
        const val COL_USER_CLAVE = "clave"      // Guardada en MD5

        // Tabla personajes
        const val TABLE_PERSONAJES = "personajes"
        const val COL_PJ_ID = "id"
        const val COL_PJ_NOMBRE = "nombre"
        const val COL_PJ_VIDA = "vida"
        const val COL_PJ_IMAGEN = "imagen"       // Ruta de imagen en dispositivo

        // Tabla armas
        const val TABLE_ARMAS = "armas"
        const val COL_ARMA_ID = "id"
        const val COL_ARMA_NOMBRE = "nombre"
        const val COL_ARMA_DANIO = "danio"
        const val COL_ARMA_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla usuarios
        val createUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_USER_ID   INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_LOGIN TEXT NOT NULL UNIQUE,
                $COL_USER_CLAVE  TEXT NOT NULL
            )
        """.trimIndent()

        // Crear tabla personajes
        val createPersonajes = """
            CREATE TABLE $TABLE_PERSONAJES (
                $COL_PJ_ID     INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PJ_NOMBRE TEXT NOT NULL,
                $COL_PJ_VIDA   INTEGER DEFAULT 100,
                $COL_PJ_IMAGEN TEXT
            )
        """.trimIndent()

        // Crear tabla armas
        val createArmas = """
            CREATE TABLE $TABLE_ARMAS (
                $COL_ARMA_ID     INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ARMA_NOMBRE TEXT NOT NULL,
                $COL_ARMA_DANIO  INTEGER DEFAULT 10,
                $COL_ARMA_IMAGEN TEXT
            )
        """.trimIndent()

        db.execSQL(createUsuarios)
        db.execSQL(createPersonajes)
        db.execSQL(createArmas)

        // Insertar usuario de prueba: admin / admin123 (guardado en MD5)
        insertarUsuarioInicial(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERSONAJES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ARMAS")
        onCreate(db)
    }

    // ─── Utilidad MD5 ────────────────────────────────────────────────────────
    fun md5(texto: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val bytes = digest.digest(texto.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // ─── Usuarios ─────────────────────────────────────────────────────────────
    private fun insertarUsuarioInicial(db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put(COL_USER_LOGIN, "admin")
            put(COL_USER_CLAVE, md5("admin123"))
        }
        db.insert(TABLE_USUARIOS, null, values)
    }

    /**
     * Comprueba credenciales:
     *  1. Busca el usuario por login
     *  2. Compara el MD5 de la clave introducida con la clave almacenada
     */
    fun validarUsuario(login: String, clave: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            arrayOf(COL_USER_CLAVE),
            "$COL_USER_LOGIN = ?",
            arrayOf(login),
            null, null, null
        )
        var valido = false
        if (cursor.moveToFirst()) {
            val claveGuardada = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_CLAVE))
            valido = (claveGuardada == md5(clave))
        }
        cursor.close()
        db.close()
        return valido
    }
}