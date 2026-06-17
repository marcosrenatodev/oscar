import { Router } from "express";
import { VotoController } from "../controllers/votoController";

const router = Router();
const votoController = new VotoController();

router.post("/votos/confirmar", (req, res) => votoController.confirmar(req, res));
router.get("/usuarios/:id/voto", (req, res) => votoController.buscarVotoDoUsuario(req, res));

export default router;
