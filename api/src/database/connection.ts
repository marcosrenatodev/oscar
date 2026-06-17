import Database from "better-sqlite3";
import dotenv from "dotenv";
import path from "path";

dotenv.config();

const databaseFile = process.env.DATABASE_FILE || "./database.sqlite";
const databasePath = path.resolve(process.cwd(), databaseFile);

export const db = new Database(databasePath);

db.pragma("foreign_keys = ON");
