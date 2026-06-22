package com.ufpr.oscar_app.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.ufpr.oscar_app.data.db.DBHelper
import com.ufpr.oscar_app.model.Diretor
import com.ufpr.oscar_app.model.Filme
import com.ufpr.oscar_app.model.VotoLocal

class VotoLocalDAO(private val context: Context) {

    private val dbHelper = DBHelper(context)

    /**
     * Garante que existe uma linha de voto para o usuário antes de atualizar.
     */
    private fun garantirLinha(db: SQLiteDatabase, usuarioId: Int) {
        db.execSQL(
            "INSERT OR IGNORE INTO ${DBHelper.TABLE_VOTO_LOCAL} (usuarioId, confirmado) VALUES (?, 0)",
            arrayOf<Any>(usuarioId)
        )
    }

    /**
     * Salva localmente o voto no filme, preservando o voto no diretor.
     */
    fun salvarFilme(usuarioId: Int, filme: Filme) {
        val db = dbHelper.writableDatabase
        garantirLinha(db, usuarioId)

        val values = ContentValues().apply {
            put("filmeId", filme.id)
            put("filmeNome", filme.nome)
            put("filmeGenero", filme.genero)
            put("filmeFoto", filme.foto)
        }

        db.update(DBHelper.TABLE_VOTO_LOCAL, values, "usuarioId = ?", arrayOf(usuarioId.toString()))
        db.close()
    }

    /**
     * Salva localmente o voto no diretor, preservando o voto no filme.
     */
    fun salvarDiretor(usuarioId: Int, diretor: Diretor) {
        val db = dbHelper.writableDatabase
        garantirLinha(db, usuarioId)

        val values = ContentValues().apply {
            put("diretorId", diretor.id)
            put("diretorNome", diretor.nome)
        }

        db.update(DBHelper.TABLE_VOTO_LOCAL, values, "usuarioId = ?", arrayOf(usuarioId.toString()))
        db.close()
    }

    /**
     * Marca o voto do usuário como confirmado, usado no bloqueio pós-confirmação.
     */
    fun marcarConfirmado(usuarioId: Int) {
        val db = dbHelper.writableDatabase
        garantirLinha(db, usuarioId)

        val values = ContentValues().apply { put("confirmado", 1) }

        db.update(DBHelper.TABLE_VOTO_LOCAL, values, "usuarioId = ?", arrayOf(usuarioId.toString()))
        db.close()
    }

    /**
     * Marca como confirmado a partir da resposta do servidor, gravando os ids votados
     * (usado quando o GET indica que o usuário já votou em uma sessão anterior).
     */
    fun marcarConfirmadoComVotos(usuarioId: Int, filmeId: String?, diretorId: String?) {
        val db = dbHelper.writableDatabase
        garantirLinha(db, usuarioId)

        val values = ContentValues().apply {
            put("confirmado", 1)
            if (filmeId != null) put("filmeId", filmeId)
            if (diretorId != null) put("diretorId", diretorId)
        }

        db.update(DBHelper.TABLE_VOTO_LOCAL, values, "usuarioId = ?", arrayOf(usuarioId.toString()))
        db.close()
    }

    /**
     * Remove o voto local de um usuário sem afetar os registros dos demais usuários.
     */
    fun excluirPorUsuario(usuarioId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            DBHelper.TABLE_VOTO_LOCAL,
            "usuarioId = ?",
            arrayOf(usuarioId.toString())
        )
        db.close()
    }

    /**
     * Lê o voto local do usuário, ou null se ainda não houver voto registrado.
     */
    fun buscarPorUsuario(usuarioId: Int): VotoLocal? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DBHelper.TABLE_VOTO_LOCAL,
            null,
            "usuarioId = ?",
            arrayOf(usuarioId.toString()),
            null,
            null,
            null
        )

        var voto: VotoLocal? = null

        if (cursor.moveToFirst()) {
            voto = VotoLocal(
                usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow("usuarioId")),
                filmeId = cursor.getString(cursor.getColumnIndexOrThrow("filmeId")),
                filmeNome = cursor.getString(cursor.getColumnIndexOrThrow("filmeNome")),
                filmeGenero = cursor.getString(cursor.getColumnIndexOrThrow("filmeGenero")),
                filmeFoto = cursor.getString(cursor.getColumnIndexOrThrow("filmeFoto")),
                diretorId = cursor.getString(cursor.getColumnIndexOrThrow("diretorId")),
                diretorNome = cursor.getString(cursor.getColumnIndexOrThrow("diretorNome")),
                confirmado = cursor.getInt(cursor.getColumnIndexOrThrow("confirmado")) == 1
            )
        }

        cursor.close()
        db.close()
        return voto
    }
}
