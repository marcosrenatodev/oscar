import { db } from "../database/connection";
import { Voto } from "../types";

export class VotoRepository {
  buscarPorUsuarioId(usuarioId: number): Voto | undefined {
    return db.prepare("SELECT * FROM votos WHERE usuario_id = ?").get(usuarioId) as Voto | undefined;
  }

  criar(usuarioId: number, filmeId: string, diretorId: string, tokenUsado: number): Voto {
    const criadoEm = new Date().toISOString();

    const result = db
      .prepare(
        `
          INSERT INTO votos (usuario_id, filme_id, diretor_id, token_usado, criado_em)
          VALUES (?, ?, ?, ?, ?)
        `
      )
      .run(usuarioId, filmeId, diretorId, tokenUsado, criadoEm);

    return {
      id: Number(result.lastInsertRowid),
      usuario_id: usuarioId,
      filme_id: filmeId,
      diretor_id: diretorId,
      token_usado: tokenUsado,
      criado_em: criadoEm
    };
  }
}
