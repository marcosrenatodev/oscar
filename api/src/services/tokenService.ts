import { TokenRepository } from "../repositories/tokenRepository";
import { TokenVotacao } from "../types";

export class TokenService {
  constructor(private readonly tokenRepository = new TokenRepository()) {}

  gerarToken(): number {
    return Math.floor(Math.random() * 101);
  }

  gerarESalvar(usuarioId: number): number {
    const token = this.gerarToken();
    this.tokenRepository.criar(usuarioId, token);
    return token;
  }

  buscarTokenValido(usuarioId: number, token: number): TokenVotacao | undefined {
    return this.tokenRepository.buscarTokenValido(usuarioId, token);
  }

  marcarComoUsado(tokenId: number): void {
    this.tokenRepository.marcarComoUsado(tokenId);
  }
}
