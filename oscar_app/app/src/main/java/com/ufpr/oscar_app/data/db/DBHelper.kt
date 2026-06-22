package com.ufpr.oscar_app.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "oscar_app.db"
        const val DATABASE_VERSION = 1
        const val TABLE_VOTO_LOCAL = "voto_local"
    }

    /**
     * Cria a tabela que guarda o voto local do usuário (uma linha por usuário).
     */
    override fun onCreate(db: SQLiteDatabase) {
        val createVotoLocal = """
            CREATE TABLE $TABLE_VOTO_LOCAL (
                usuarioId INTEGER PRIMARY KEY,
                filmeId TEXT,
                filmeNome TEXT,
                filmeGenero TEXT,
                filmeFoto TEXT,
                diretorId TEXT,
                diretorNome TEXT,
                confirmado INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createVotoLocal)
    }

    /**
     * Recria a tabela quando a versão do banco muda.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VOTO_LOCAL")
        onCreate(db)
    }
}
