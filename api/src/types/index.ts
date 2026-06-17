export interface Usuario {
  id: number;
  nome: string;
  login: string;
  senha: string;
  criado_em: string;
}

export interface UsuarioPublico {
  id: number;
  login: string;
  nome: string;
}

export interface TokenVotacao {
  id: number;
  usuario_id: number;
  token: number;
  usado: number;
  criado_em: string;
}

export interface Voto {
  id: number;
  usuario_id: number;
  filme_id: string;
  diretor_id: string;
  token_usado: number;
  criado_em: string;
}

export interface LoginDTO {
  login?: string;
  senha?: string;
}

export interface ConfirmarVotoDTO {
  usuarioId?: number;
  filmeId?: string;
  diretorId?: string;
  token?: number;
}

export interface ApiResponse<T = unknown> {
  sucesso: boolean;
  mensagem?: string;
  data?: T;
}
