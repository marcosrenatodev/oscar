import { db } from "../database/connection";
import { TokenVotacao } from "../types";

export class TokenRepository {
  criar(usuarioId: number, token: number): TokenVotacao {
    const criadoEm = new Date().toISOString();

    const result = db
      .prepare(
        "INSERT INTO tokens_votacao (usuario_id, token, usado, criado_em) VALUES (?, ?, 0, ?)"
      )
      .run(usuarioId, token, criadoEm);

    return {
      id: Number(result.lastInsertRowid),
      usuario_id: usuarioId,
      token,
      usado: 0,
      criado_em: criadoEm
    };
  }

  buscarTokenValido(usuarioId: number, token: number): TokenVotacao | undefined {
    return db
      .prepare(
        "SELECT * FROM tokens_votacao WHERE usuario_id = ? AND token = ? AND usado = 0 ORDER BY id DESC LIMIT 1"
      )
      .get(usuarioId, token) as TokenVotacao | undefined;
  }

  marcarComoUsado(tokenId: number): void {
    db.prepare("UPDATE tokens_votacao SET usado = 1 WHERE id = ?").run(tokenId);
  }
}
