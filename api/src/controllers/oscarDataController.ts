import { Request, Response } from "express";

const FILMES_URL = "http://200.236.3.97/filme.json";
const DIRETORES_URL = "http://200.236.3.97/diretor.json";

async function buscarJsonExterno(url: string): Promise<unknown> {
  const resposta = await fetch(url);

  if (!resposta.ok) {
    throw new Error("ERRO_DADOS_EXTERNOS");
  }

  return resposta.json();
}

export class OscarDataController {
  async listarFilmes(_req: Request, res: Response): Promise<Response> {
    try {
      const dados = await buscarJsonExterno(FILMES_URL);
      return res.status(200).json(dados);
    } catch {
      return res.status(500).json({
        sucesso: false,
        mensagem: "Erro ao buscar filmes"
      });
    }
  }

  async listarDiretores(_req: Request, res: Response): Promise<Response> {
    try {
      const dados = await buscarJsonExterno(DIRETORES_URL);
      return res.status(200).json(dados);
    } catch {
      return res.status(500).json({
        sucesso: false,
        mensagem: "Erro ao buscar diretores"
      });
    }
  }
}
