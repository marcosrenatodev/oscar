import { Router } from "express";
import { OscarDataController } from "../controllers/oscarDataController";

const router = Router();
const oscarDataController = new OscarDataController();

router.get("/filmes", (req, res) => {
  void oscarDataController.listarFilmes(req, res);
});

router.get("/diretores", (req, res) => {
  void oscarDataController.listarDiretores(req, res);
});

export default router;
