import { db } from "./connection";
import { createSchema } from "./schema";

const usuariosIniciais = [
  { nome: "Usuário 1", login: "usuario1", senha: "123456" },
  { nome: "Usuário 2", login: "usuario2", senha: "123456" },
  { nome: "Usuário 3", login: "usuario3", senha: "123456" },
  { nome: "Usuário 4", login: "usuario4", senha: "123456" },
  { nome: "Usuário 5", login: "usuario5", senha: "123456" }
];

export function seedDatabase(): void {
  createSchema();

  const usuarioCount = db.prepare("SELECT COUNT(*) as total FROM usuarios").get() as { total: number };

  if (usuarioCount.total > 0) {
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
      filme_id: "10",
      diretor_id: "15",
      token_usado: 42,
      criado_em: agora
    });
  });

  transaction();
}

if (require.main === module) {
  seedDatabase();
  console.log("Seed executado com sucesso.");
}
