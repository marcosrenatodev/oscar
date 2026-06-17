import { Request, Response } from "express";
import { AuthService } from "../services/authService";
import { LoginDTO } from "../types";

const authService = new AuthService();

export class AuthController {
  login(req: Request<object, object, LoginDTO>, res: Response): Response {
    try {
      const resultado = authService.login(req.body);

      if (!resultado) {
        return res.status(401).json({
          sucesso: false,
          mensagem: "Login ou senha inválidos"
        });
      }

      return res.status(200).json({
        sucesso: true,
        mensagem: "Login realizado com sucesso",
        usuario: resultado.usuario,
        token: resultado.token
      });
    } catch (error) {
      if (error instanceof Error && error.message === "LOGIN_SENHA_OBRIGATORIOS") {
        return res.status(400).json({
          sucesso: false,
          mensagem: "Login e senha são obrigatórios"
        });
      }

      return res.status(500).json({
        sucesso: false,
        mensagem: "Erro interno"
      });
    }
  }
}
