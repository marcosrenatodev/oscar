import { db } from "../database/connection";
import { UsuarioRepository } from "../repositories/usuarioRepository";
import { VotoRepository } from "../repositories/votoRepository";
import { ConfirmarVotoDTO, Voto } from "../types";
import { TokenService } from "./tokenService";

export class VotoService {
  constructor(
    private readonly usuarioRepository = new UsuarioRepository(),
    private readonly votoRepository = new VotoRepository(),
    private readonly tokenService = new TokenService()
  ) {}

  confirmar(dto: ConfirmarVotoDTO): void {
    const tokenInformado = dto.token !== undefined && dto.token !== null && String(dto.token).trim() !== "";
    const usuarioId = Number(dto.usuarioId);
    const token = Number(dto.token);
    const filmeId = dto.filmeId?.trim();
    const diretorId = dto.diretorId?.trim();

    if (
      !Number.isInteger(usuarioId) ||
      usuarioId <= 0 ||
      !filmeId ||
      !diretorId ||
      !tokenInformado ||
      !Number.isInteger(token)
    ) {
      throw new Error("DADOS_OBRIGATORIOS");
    }

    const usuario = this.usuarioRepository.buscarPorId(usuarioId);

    if (!usuario) {
      throw new Error("USUARIO_NAO_ENCONTRADO");
    }

    const votoExistente = this.votoRepository.buscarPorUsuarioId(usuarioId);

    if (votoExistente) {
      throw new Error("VOTO_DUPLICADO");
    }

    const tokenValido = this.tokenService.buscarTokenValido(usuarioId, token);

    if (!tokenValido) {
      throw new Error("TOKEN_INVALIDO");
    }

    const transaction = db.transaction(() => {
      this.votoRepository.criar(usuarioId, filmeId, diretorId, token);
      this.tokenService.marcarComoUsado(tokenValido.id);
    });

    transaction();
  }

  buscarVotoDoUsuario(usuarioId: number): Voto | undefined {
    return this.votoRepository.buscarPorUsuarioId(usuarioId);
  }
}
