# oscar-api

API REST do Sistema Central do Oscar App, criada com Node.js, Express, TypeScript e SQLite.

## Tecnologias

- Node.js
- Express
- TypeScript
- SQLite com `better-sqlite3`
- `dotenv`
- `cors`
- `tsx` para desenvolvimento
- `pnpm` como gerenciador de pacotes

## Instalação e execução

1. Instale as dependências:

```bash
pnpm install
```

2. Copie o arquivo de ambiente:

```bash
cp .env.example .env
```

3. Rode a API em desenvolvimento:

```bash
pnpm run dev
```

4. Teste a rota de saúde:

```bash
curl http://localhost:3000/health
```

A API roda por padrão em `0.0.0.0:3000`. No Android Emulator, use `http://10.0.2.2:3000`. Em celular físico, use o IP local do computador, por exemplo `http://192.168.0.25:3000`.

## Scripts

- `pnpm run dev`: inicia a API com reload automático.
- `pnpm run build`: compila TypeScript para `dist`.
- `pnpm start`: executa a versão compilada.
- `pnpm run seed`: cria tabelas e insere os dados iniciais.

## Dados iniciais

Ao iniciar, o banco SQLite local é criado automaticamente e recebe 5 usuários:

- `usuario1` / `123456`
- `usuario2` / `123456`
- `usuario3` / `123456` já possui voto confirmado
- `usuario4` / `123456`
- `usuario5` / `123456`

## Endpoints

Também existem exemplos prontos na pasta `requests/`.

### GET /health

```bash
curl http://localhost:3000/health
```

Resposta:

```json
{
  "status": "ok"
}
```

### POST /auth/login

```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"usuario1","senha":"123456"}'
```

Resposta de sucesso:

```json
{
  "sucesso": true,
  "mensagem": "Login realizado com sucesso",
  "usuario": {
    "id": 1,
    "login": "usuario1",
    "nome": "Usuário 1"
  },
  "token": 57
}
```

### POST /votos/confirmar

Use o token retornado no login:

```bash
curl -X POST http://localhost:3000/votos/confirmar \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":1,"filmeId":"10","diretorId":"15","token":57}'
```

Resposta:

```json
{
  "sucesso": true,
  "mensagem": "Voto confirmado com sucesso"
}
```

### GET /usuarios/:id/voto

```bash
curl http://localhost:3000/usuarios/1/voto
```

Resposta quando ainda não votou:

```json
{
  "sucesso": true,
  "jaVotou": false,
  "voto": null
}
```

Resposta quando já votou:

```json
{
  "sucesso": true,
  "jaVotou": true,
  "voto": {
    "filmeId": "10",
    "diretorId": "15"
  }
}
```

### GET /filmes

Retorna os dados do JSON externo `http://200.236.3.97/filme.json`.

```bash
curl http://localhost:3000/filmes
```

### GET /diretores

Retorna os dados do JSON externo `http://200.236.3.97/diretor.json`.

```bash
curl http://localhost:3000/diretores
```
