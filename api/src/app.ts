import cors from "cors";
import express, { Request, Response } from "express";
import authRoutes from "./routes/authRoutes";
import oscarDataRoutes from "./routes/oscarDataRoutes";
import votoRoutes from "./routes/votoRoutes";

export const app = express();

app.use(cors());
app.use(express.json());

app.get("/health", (_req: Request, res: Response) => {
  return res.status(200).json({ status: "ok" });
});

app.use("/auth", authRoutes);
app.use(votoRoutes);
app.use(oscarDataRoutes);

app.use((_req: Request, res: Response) => {
  return res.status(404).json({
    sucesso: false,
    mensagem: "Rota não encontrada"
  });
});
