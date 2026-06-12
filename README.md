# PocketMarket Backend

API REST do **PocketMarket**, um marketplace acadêmico para gerenciamento, compra, venda, leilão, coleção, favoritos e troca de cartas Pokémon TCG.

O projeto foi desenvolvido em **Java 21** com **Spring Boot**, utilizando autenticação via **JWT**, persistência com **Spring Data JPA**, banco **PostgreSQL**, documentação com **Swagger/OpenAPI**, integração com a **Pokémon TCG API** e suporte a execução via **Docker**.

---

## Sumário

- [Sobre o projeto](#sobre-o-projeto)
- [Principais funcionalidades](#principais-funcionalidades)
- [Stack utilizada](#stack-utilizada)
- [Arquitetura geral](#arquitetura-geral)
- [Estrutura de pastas](#estrutura-de-pastas)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do ambiente](#configuração-do-ambiente)
- [Como rodar localmente](#como-rodar-localmente)
- [Como rodar com Docker](#como-rodar-com-docker)
- [Documentação Swagger](#documentação-swagger)
- [Endpoints principais](#endpoints-principais)
- [Autenticação](#autenticação)
- [Testes e cobertura](#testes-e-cobertura)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Observações importantes](#observações-importantes)
- [Licença](#licença)

---

## Sobre o projeto

O **PocketMarket Backend** é a API responsável pelas regras de negócio do sistema PocketMarket. Ele permite que usuários cadastrem contas, façam login, adicionem cartas à própria coleção, consultem cartas do catálogo, criem anúncios de venda, comprem cartas, participem de leilões, favoritem cartas e realizem propostas de troca.

A aplicação segue uma arquitetura em camadas baseada em:

- **Controllers** para exposição dos endpoints REST.
- **Services** para regras de negócio.
- **Repositories** para acesso ao banco de dados.
- **DTOs** para entrada e saída de dados.
- **Mappers** para conversão entre entidades e DTOs.
- **Security** para autenticação e autorização via JWT.
- **Exceptions** para tratamento padronizado de erros.

---

## Principais funcionalidades

### Autenticação e segurança

- Cadastro de usuários.
- Login com geração de token JWT.
- Rotas protegidas com Bearer Token.
- Integração com Spring Security.
- Hash de senha usando os mecanismos configurados na camada de autenticação.

### Usuários

- Buscar usuário por ID.
- Atualizar perfil de usuário.
- Consultar créditos do usuário autenticado.
- Adicionar créditos a um usuário.

### Cartas e catálogo

- Listar cartas cadastradas no sistema.
- Buscar carta por ID.
- Remover carta.
- Consultar catálogo de cartas.
- Buscar carta por nome.
- Buscar carta por ID externo da Pokémon TCG API.

### User Cards

- Adicionar carta à coleção pessoal do usuário.
- Listar cartas do usuário autenticado.
- Listar todas as cartas de usuários no sistema.
- Buscar uma user card por ID.
- Remover carta da coleção do usuário.

### Coleção

- Adicionar carta à coleção.
- Listar coleção do usuário autenticado.
- Remover carta da coleção.

### Favoritos

- Favoritar carta.
- Listar favoritos.
- Remover carta dos favoritos.

### Listings

- Criar anúncio de venda por preço fixo.
- Criar anúncio de leilão.
- Listar anúncios com paginação.
- Detalhar anúncio por ID.
- Cancelar anúncio.

### Compras

- Comprar uma listagem.
- Listar minhas compras.
- Listar minhas vendas.

### Leilões

- Criar lance em leilão.
- Listar lances de um leilão.
- Finalizar leilão.

### Propostas de troca

- Criar proposta de troca.
- Listar propostas enviadas.
- Listar propostas recebidas.
- Aceitar proposta de troca.

### Uploads

- Configuração para armazenamento local em pasta `uploads`.
- Limite de arquivo configurado em `5MB`.

---

## Stack utilizada

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.6 |
| API REST | Spring Web MVC |
| Segurança | Spring Security + JWT |
| Persistência | Spring Data JPA / Hibernate |
| Banco de dados | PostgreSQL |
| Documentação | Springdoc OpenAPI / Swagger UI |
| Mapeamento DTO | ModelMapper |
| Boilerplate | Lombok |
| Containerização | Docker / Docker Compose |
| Testes | Spring Boot Test |
| Cobertura | JaCoCo |
| API externa | Pokémon TCG API |

---

## Arquitetura geral

```text
Client / Frontend / Postman
        |
        | HTTP REST
        v
Spring Boot API
        |
        | Controllers
        v
Services
        |
        | Business Rules
        v
Repositories
        |
        | JPA / Hibernate
        v
PostgreSQL
```

Fluxo de autenticação:

```text
Usuário faz login
        |
        v
AuthController
        |
        v
AuthService valida credenciais
        |
        v
JwtService gera token
        |
        v
Cliente envia Authorization: Bearer <token>
        |
        v
Security Filter valida token nas rotas protegidas
```

---

## Estrutura de pastas

Estrutura principal observada no backend:

```text
src/main/java/com/pocketmarket
├── auction
│   ├── dto
│   ├── mapper
│   ├── scheduler
│   ├── service
│   ├── validator
│   ├── AuctionBid.java
│   ├── AuctionBidController.java
│   └── AuctionBidRepository.java
├── auth
│   ├── dto
│   ├── AuthController.java
│   ├── AuthService.java
│   └── JwtService.java
├── cardcatalog
│   ├── dto
│   ├── CardCatalogController.java
│   └── CardCatalogService.java
├── cards
│   ├── dto
│   ├── CardController.java
│   ├── CardService.java
│   └── CardRepository.java
├── collection
│   ├── dto
│   ├── CollectionController.java
│   ├── CollectionService.java
│   └── CollectionRepository.java
├── config
├── controleTeste
├── enums
├── exceptions
├── favorite
│   ├── dto
│   ├── FavoriteController.java
│   ├── FavoriteService.java
│   └── FavoriteRepository.java
├── listing
│   ├── dto
│   ├── Listing.java
│   ├── ListingController.java
│   ├── ListingMapper.java
│   ├── ListingRepository.java
│   └── ListingService.java
├── purchase
│   ├── dto
│   ├── PurchaseController.java
│   ├── PurchaseService.java
│   └── PurchaseRepository.java
├── security
├── trade
│   ├── dto
│   ├── TradeOfferController.java
│   ├── TradeOfferService.java
│   └── TradeOfferRepository.java
├── upload
├── user
│   ├── dtos
│   ├── UserController.java
│   ├── UserService.java
│   └── UserRepository.java
├── usercards
│   ├── dto
│   ├── UserCardController.java
│   ├── UserCardService.java
│   └── UserCardRepository.java
└── PocketmarketApplication.java
```

Recursos principais:

```text
src/main/resources
├── application.yaml
├── application.yml.example
└── db/migrations
    └── V8__create_trade_tables.sql
```

---

## Pré-requisitos

Para rodar o projeto localmente, instale:

- **Java 21**
- **Git**
- **Docker** e **Docker Compose**
- **PostgreSQL** local ou banco PostgreSQL externo, como Supabase
- Uma IDE, como IntelliJ IDEA, VS Code ou Eclipse
- Postman, Insomnia ou outro cliente HTTP para testar a API

Verifique as versões:

```bash
java -version
git --version
docker --version
docker compose version
```

---

## Configuração do ambiente

Clone o repositório:

```bash
git clone https://github.com/ViniciusS4ntos/PocketMarket.git
cd PocketMarket
```

Crie o arquivo `.env` na raiz do projeto com base no `.env.example`:

```bash
cp .env.example .env
```

Exemplo de `.env` para banco PostgreSQL local:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pocketmarket
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=sua_senha
SPRING_JPA_HIBERNATE_DDL_AUTO=update
API_SECURITY_TOKEN_SECRET=coloque_uma_chave_secreta_grande_aqui
POKEMON_TCG_API_KEY=
```

> A variável `POKEMON_TCG_API_KEY` pode ficar vazia caso você esteja usando apenas endpoints públicos da Pokémon TCG API. Caso tenha uma chave, preencha o valor.

---

## Como rodar localmente

### 1. Subir um PostgreSQL local

Caso você já tenha PostgreSQL instalado, crie o banco:

```sql
CREATE DATABASE pocketmarket;
```

Caso prefira subir apenas o PostgreSQL via Docker:

```bash
docker run --name pocketmarket-postgres \
  -e POSTGRES_DB=pocketmarket \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16
```

Nesse caso, use este `.env`:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pocketmarket
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=update
API_SECURITY_TOKEN_SECRET=coloque_uma_chave_secreta_grande_aqui
POKEMON_TCG_API_KEY=
```

### 2. Instalar dependências

No Linux/macOS:

```bash
./mvnw clean install
```

No Windows:

```bash
mvnw.cmd clean install
```

### 3. Rodar a aplicação

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

---

## Como rodar com Docker

O projeto possui `Dockerfile` e `docker-compose.yml` para build e execução da aplicação.

> Importante: o `docker-compose.yml` atual sobe a aplicação, mas não define um serviço PostgreSQL interno. Portanto, ele espera que as variáveis do `.env` apontem para um banco PostgreSQL acessível pela aplicação, como Supabase ou outro PostgreSQL externo.

### 1. Criar o `.env`

```bash
cp .env.example .env
```

Exemplo usando Supabase ou banco externo:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=sua_senha
SPRING_JPA_HIBERNATE_DDL_AUTO=update
API_SECURITY_TOKEN_SECRET=coloque_uma_chave_secreta_grande_aqui
POKEMON_TCG_API_KEY=
```

### 2. Buildar e subir a aplicação

```bash
docker compose up --build
```

A API ficará disponível em:

```text
http://localhost:8080
```

### 3. Parar os containers

```bash
docker compose down
```

### 4. Parar e remover volumes

```bash
docker compose down -v
```

---

## Documentação Swagger

Com a aplicação rodando, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

Também pode funcionar em algumas configurações como:

```text
http://localhost:8080/swagger-ui.html
```

A documentação Swagger permite visualizar e testar os endpoints da API, incluindo rotas protegidas com JWT.

Para rotas autenticadas, clique em **Authorize** e informe:

```text
Bearer SEU_TOKEN_JWT
```

---

## Endpoints principais

Base URL local:

```text
http://localhost:8080
```

Prefixo principal da API:

```text
/api/v1
```

---

### Auth

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| POST | `/api/v1/auth/register` | Não | Cadastra um novo usuário |
| POST | `/api/v1/auth/login` | Não | Realiza login e retorna JWT |

Exemplo de cadastro:

```json
{
  "name": "Nome Exemplo",
  "email": "exemplo@email.com",
  "password": "123456"
}
```

Exemplo de login:

```json
{
  "email": "exemplo@email.com",
  "password": "123456"
}
```

---

### Users

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| GET | `/api/v1/users/{id}` | Não/Parcial | Busca usuário por ID |
| PUT | `/api/v1/users/{id}` | Sim | Atualiza perfil do usuário |
| GET | `/api/v1/users/me/credits` | Sim | Consulta créditos do usuário autenticado |
| PATCH | `/api/v1/users/credit/{userId}` | Sim | Adiciona créditos a um usuário |

---

### Cards

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| GET | `/api/v1/cards` | Não | Lista cartas do sistema |
| GET | `/api/v1/cards/{id}` | Não | Busca carta por ID |
| DELETE | `/api/v1/cards/{id}` | Não/Restrito pela regra interna | Remove carta |

---

### Card Catalog

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| GET | `/api/v1/card-catalog` | Não | Lista cartas do catálogo com paginação |
| GET | `/api/v1/card-catalog/search?name={name}` | Não | Busca cartas por nome |
| GET | `/api/v1/card-catalog/{externalId}` | Não | Busca carta por ID externo |

Exemplo:

```text
GET /api/v1/card-catalog/search?name=pikachu
```

---

### User Cards

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| POST | `/api/v1/user-cards` | Sim | Adiciona carta à coleção do usuário |
| GET | `/api/v1/user-cards/me` | Sim | Lista minhas cartas |
| GET | `/api/v1/user-cards` | Não | Lista todas as cartas de usuários |
| GET | `/api/v1/user-cards/{id}` | Não | Busca user card por ID |
| DELETE | `/api/v1/user-cards/{id}` | Sim | Remove user card |

---

### Collection

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| POST | `/api/v1/collection` | Sim | Adiciona carta à coleção |
| GET | `/api/v1/collection` | Sim | Lista coleção do usuário |
| DELETE | `/api/v1/collection/{userCardId}` | Sim | Remove carta da coleção |

---

### Favorites

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| POST | `/api/v1/favorites/{cardId}` | Sim | Adiciona carta aos favoritos |
| GET | `/api/v1/favorites` | Sim | Lista favoritos |
| DELETE | `/api/v1/favorites/{cardId}` | Sim | Remove carta dos favoritos |

---

### Listings

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| POST | `/api/v1/listings/sale/{userCardId}` | Sim | Cria anúncio de venda |
| POST | `/api/v1/listings/auction/{userCardId}` | Sim | Cria anúncio de leilão |
| GET | `/api/v1/listings` | Não | Lista anúncios com paginação |
| GET | `/api/v1/listings/{id}` | Não | Detalha anúncio |
| PATCH | `/api/v1/listings/{id}/cancel` | Sim | Cancela anúncio |

Paginação:

```text
GET /api/v1/listings?page=0&size=20
```

---

### Purchases

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| POST | `/api/v1/purchases/{listingId}/buy` | Sim | Compra uma listagem |
| GET | `/api/v1/purchases/my-purchases` | Sim | Lista minhas compras |
| GET | `/api/v1/purchases/my-sales` | Sim | Lista minhas vendas |

---

### Auctions

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| POST | `/api/v1/auctions/{listingId}/bids` | Sim | Cria lance em leilão |
| GET | `/api/v1/auctions/{listingId}/bids` | Não | Lista lances do leilão |
| POST | `/api/v1/auctions/{listingId}/finish` | Não/Regra interna | Finaliza leilão |

---

### Trade Offers

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| POST | `/api/v1/trade-offers` | Sim | Cria proposta de troca |
| GET | `/api/v1/trade-offers/sent` | Sim | Lista propostas enviadas |
| GET | `/api/v1/trade-offers/received` | Sim | Lista propostas recebidas |
| PATCH | `/api/v1/trade-offers/{id}/accept` | Sim | Aceita proposta de troca |

---

## Autenticação

Após realizar login, a API retorna um token JWT.

Use o token em todas as rotas protegidas no header:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

Exemplo com cURL:

```bash
curl -X GET http://localhost:8080/api/v1/users/me/credits \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

---

## Testes e cobertura

O projeto possui configuração de testes com Spring Boot Test e cobertura com JaCoCo.

Rodar testes:

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

Gerar relatório de cobertura:

```bash
./mvnw clean test
```

Relatório JaCoCo:

```text
target/site/jacoco/index.html
```

---

## Variáveis de ambiente

| Variável | Obrigatória | Descrição | Exemplo |
| --- | --- | --- | --- |
| `SPRING_DATASOURCE_URL` | Sim | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/pocketmarket` |
| `SPRING_DATASOURCE_USERNAME` | Sim | Usuário do banco | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Sim | Senha do banco | `postgres` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Não | Estratégia de criação/atualização do schema | `update` |
| `API_SECURITY_TOKEN_SECRET` | Sim | Chave secreta para geração e validação JWT | `minha-chave-secreta` |
| `POKEMON_TCG_API_KEY` | Não | Chave da Pokémon TCG API | vazio ou sua key |

---

## Observações importantes

- O projeto usa `application.yaml` com importação opcional do arquivo `.env`.
- O fuso horário configurado no Jackson é `America/Recife`.
- O limite de upload configurado é de `5MB`.
- O caminho público de uploads está configurado como `/uploads`.
- O `docker-compose.yml` atual não sobe PostgreSQL automaticamente; ele apenas sobe a aplicação e espera um banco externo configurado via `.env`.
- O `Dockerfile` utiliza build multi-stage com `eclipse-temurin:21-jdk` para build e `eclipse-temurin:21-jre` para runtime.
- A API usa paginação em endpoints como listings, purchases, auctions e user cards.
- Algumas rotas públicas ou sem anotação explícita de segurança podem ainda depender da configuração global do Spring Security.

---

## Possível fluxo de teste manual

1. Registrar usuário em `/api/v1/auth/register`.
2. Fazer login em `/api/v1/auth/login`.
3. Copiar o token JWT retornado.
4. Consultar créditos em `/api/v1/users/me/credits`.
5. Buscar uma carta no catálogo em `/api/v1/card-catalog/search?name=pikachu`.
6. Adicionar carta à coleção em `/api/v1/user-cards`.
7. Criar uma listagem de venda em `/api/v1/listings/sale/{userCardId}`.
8. Listar anúncios em `/api/v1/listings`.
9. Realizar compra em `/api/v1/purchases/{listingId}/buy`.
10. Consultar compras em `/api/v1/purchases/my-purchases`.

---

## Comandos úteis

Rodar aplicação:

```bash
./mvnw spring-boot:run
```

Rodar testes:

```bash
./mvnw test
```

Buildar JAR:

```bash
./mvnw clean package
```

Rodar JAR gerado:

```bash
java -jar target/*.jar
```

Subir com Docker Compose:

```bash
docker compose up --build
```

Parar Docker Compose:

```bash
docker compose down
```

---

## Licença

Este projeto está licenciado sob a licença **MIT**.

---

## Autor

Projeto desenvolvido no contexto acadêmico do **PocketMarket**, um sistema de marketplace de cartas Pokémon TCG.
