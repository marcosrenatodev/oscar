import { db } from "./connection";
import { createSchema } from "./schema";

const usuariosIniciais = [
  { nome: "Usuário 1", login: "usuario1", senha: "123456" },
  { nome: "Usuário 2", login: "usuario2", senha: "123456" },
  { nome: "Usuário 3", login: "usuario3", senha: "123456" },
  { nome: "Usuário 4", login: "usuario4", senha: "123456" },
  { nome: "Usuário 5", login: "usuario5", senha: "123456" }
];

export function seedDatabase(clearBeforeSeed = false): void {
  createSchema();

  const usuarioCount = db.prepare("SELECT COUNT(*) as total FROM usuarios").get() as { total: number };

  if (!clearBeforeSeed && usuarioCount.total > 0) {
    return;
  }

  const agora = new Date().toISOString();
  const insertUsuario = db.prepare(`
    INSERT INTO usuarios (nome, login, senha, criado_em)
    VALUES (@nome, @login, @senha, @criado_em)
  `);

  const insertToken = db.prepare(`
    INSERT INTO tokens_votacao (usuario_id, token, usado, criado_em)
    VALUES (@usuario_id, @token, @usado, @criado_em)
  `);

  const insertVoto = db.prepare(`
    INSERT INTO votos (usuario_id, filme_id, diretor_id, token_usado, criado_em)
    VALUES (@usuario_id, @filme_id, @diretor_id, @token_usado, @criado_em)
  `);

  const transaction = db.transaction(() => {
    if (clearBeforeSeed) {
      db.exec(`
        DELETE FROM votos;
        DELETE FROM tokens_votacao;
        DELETE FROM usuarios;
        DELETE FROM sqlite_sequence
        WHERE name IN ('votos', 'tokens_votacao', 'usuarios');
      `);
    }

    for (const usuario of usuariosIniciais) {
      insertUsuario.run({ ...usuario, criado_em: agora });
    }

    insertToken.run({
      usuario_id: 3,
      token: 42,
      usado: 1,
      criado_em: agora
    });

    insertVoto.run({
      usuario_id: 3,
      filme_id: "1",
      diretor_id: "1",
      token_usado: 42,
      criado_em: agora
    });
  });

  transaction();
}

if (require.main === module) {
  const clearBeforeSeed = process.argv.includes("--clear");
  seedDatabase(clearBeforeSeed);
  console.log(clearBeforeSeed
    ? "Banco limpo e seed executado com sucesso."
    : "Seed executado com sucesso.");
}
