import { db } from "../database/connection";
import { Usuario } from "../types";

export class UsuarioRepository {
  buscarPorLogin(login: string): Usuario | undefined {
    return db.prepare("SELECT * FROM usuarios WHERE login = ?").get(login) as Usuario | undefined;
  }

  buscarPorId(id: number): Usuario | undefined {
    return db.prepare("SELECT * FROM usuarios WHERE id = ?").get(id) as Usuario | undefined;
  }
}
