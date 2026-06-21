# ---- etapa de build: compila o TypeScript ----
FROM node:24-alpine AS build

# build-base/python3 são necessários pra compilar o better-sqlite3 (módulo nativo)
RUN apk add --no-cache python3 make g++
# habilita o pnpm via corepack (versão fixada no package.json)
RUN corepack enable

WORKDIR /usr/src/app
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm run build

# ---- etapa só pra instalar deps de produção limpas ----
# precisa ser alpine + toolchain pra compilar o better-sqlite3 pro musl
FROM node:24-alpine AS deps

RUN apk add --no-cache python3 make g++
RUN corepack enable

WORKDIR /usr/src/app
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile --prod

# ---- imagem final ----
FROM node:24-alpine

WORKDIR /usr/src/app
ENV NODE_ENV=production

COPY --from=build /usr/src/app/dist ./dist
COPY --from=deps /usr/src/app/node_modules ./node_modules
COPY package.json ./

# pasta onde o SQLite vai morar (montada como volume no compose)
RUN mkdir -p /usr/src/app/data

EXPOSE 3000
CMD ["node", "dist/server.js"]
