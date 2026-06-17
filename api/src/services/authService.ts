import { UsuarioRepository } from "../repositories/usuarioRepository";
import { LoginDTO, UsuarioPublico } from "../types";
import { TokenService } from "./tokenService";

interface LoginSucesso {
  usuario: UsuarioPublico;
  token: number;
}

export class AuthService {
  constructor(
    private readonly usuarioRepository = new UsuarioRepository(),
    private readonly tokenService = new TokenService()
  ) {}

  login(dto: LoginDTO): LoginSucesso | null {
    const login = dto.login?.trim();
    const senha = dto.senha?.trim();

    if (!login || !senha) {
      throw new Error("LOGIN_SENHA_OBRIGATORIOS");
    }

    const usuario = this.usuarioRepository.buscarPorLogin(login);

    if (!usuario || usuario.senha !== senha) {
      return null;
    }

    return {
      usuario: {
        id: usuario.id,
        login: usuario.login,
        nome: usuario.nome
      },
      token: this.tokenService.gerarESalvar(usuario.id)
    };
  }
}
