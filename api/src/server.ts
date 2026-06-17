import dotenv from "dotenv";
import { app } from "./app";
import { createSchema } from "./database/schema";
import { seedDatabase } from "./database/seed";

dotenv.config();

const port = Number(process.env.PORT) || 3000;
const host = process.env.HOST || "0.0.0.0";

createSchema();
seedDatabase();

app.listen(port, host, () => {
  console.log(`Oscar API rodando em http://${host}:${port}`);
});
