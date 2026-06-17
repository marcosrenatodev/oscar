import { Request, Response } from "express";
import { VotoService } from "../services/votoService";
import { ConfirmarVotoDTO } from "../types";

const votoService = new VotoService();

export class VotoController {
  confirmar(req: Request<object, object, ConfirmarVotoDTO>, res: Response): Response {
    try {
      votoService.confirmar(req.body);

      return res.status(201).json({
        sucesso: true,
        mensagem: "Voto confirmado com sucesso"
      });
    } catch (error) {
      if (error instanceof Error) {
        if (error.message === "DADOS_OBRIGATORIOS") {
          return res.status(400).json({
            sucesso: false,
            mensagem: "Dados obrigatórios não informados"
          });
        }

        if (error.message === "USUARIO_NAO_ENCONTRADO") {
          return res.status(400).json({
            sucesso: false,
            mensagem: "Usuário não encontrado"
          });
        }

        if (error.message === "VOTO_DUPLICADO") {
          return res.status(409).json({
            sucesso: false,
            mensagem: "Usuário já confirmou o voto"
          });
        }

        if (error.message === "TOKEN_INVALIDO") {
          return res.status(401).json({
            sucesso: false,
            mensagem: "Token inválido"
          });
        }
      }

      return res.status(500).json({
        sucesso: false,
        mensagem: "Erro interno"
      });
    }
  }

  buscarVotoDoUsuario(req: Request<{ id: string }>, res: Response): Response {
    const usuarioId = Number(req.params.id);

    if (!Number.isInteger(usuarioId) || usuarioId <= 0) {
      return res.status(400).json({
        sucesso: false,
        mensagem: "Usuário inválido"
      });
    }

    try {
      const voto = votoService.buscarVotoDoUsuario(usuarioId);

      if (!voto) {
        return res.status(200).json({
          sucesso: true,
          jaVotou: false,
          voto: null
        });
      }

      return res.status(200).json({
        sucesso: true,
        jaVotou: true,
        voto: {
          filmeId: voto.filme_id,
          diretorId: voto.diretor_id
        }
      });
    } catch {
      return res.status(500).json({
        sucesso: false,
        mensagem: "Erro interno"
      });
    }
  }
}
