# TPO — TCG CardHeaven — Grupo 3

**Fecha:** 2026-04-17
**Repositorio:** https://github.com/<org>/TPO--TCG-CardHeaven-Grupo3

## Portada

| Nombre | Legajo |
|---|---|
| Manuel Gallardo | `<legajo>` |
| `<integrante 2>` | `<legajo>` |
| `<integrante 3>` | `<legajo>` |
| `<integrante 4>` | `<legajo>` |

---

## 1. Diagrama de arquitectura

```
                              ┌──────────────┐
                              │   Cliente    │  (Postman / Swagger UI / Front)
                              └──────┬───────┘
                                     │ HTTP  Authorization: Bearer <JWT>
                                     ▼
        ┌────────────────── Spring Security Filter Chain ──────────────────┐
        │                                                                  │
        │  CorsFilter ─► JwtAuthenticationFilter ─► UsernamePasswordAuth.  │
        │                    │                                             │
        │                    ▼                                             │
        │          JwtService.validate(token)                              │
        │                    │                                             │
        │                    ▼                                             │
        │          SecurityContextHolder  (principal = userId, roles)      │
        └──────────────────────────┬───────────────────────────────────────┘
                                   ▼
                        @RestController (capa Web)
                 AuthController · UserController · CardController
                 CartController · OrderController · DeckController
                 GameController · SetController   · DiscountController
                 RoleController · AdminController
                                   │ @PreAuthorize("hasRole('ADMIN')") / SpEL por userId
                                   ▼
                        Service (reglas de negocio)
                                   │
                                   ▼
                     Repository (Spring Data JPA)
                                   │
                                   ▼
                        PostgreSQL 16 (Flyway V1)
```

**Nota:** el enunciado pide MySQL pero el proyecto usa **PostgreSQL 16** (ver `docker-compose.yml` y `application.properties`). Confirmar con cátedra o migrar antes de entregar.

---

## 2. Listado de entidades (DER) — mapa Entidad ↔ Clase Java ↔ Tabla

| # | Entidad (DER) | Clase Java | Tabla BD |
|---|---|---|---|
| 1 | Role | `com.tcgtrader.entity.Role` | `roles` |
| 2 | User | `com.tcgtrader.entity.User` | `users` |
| 3 | Address | `com.tcgtrader.entity.Address` | `addresses` |
| 4 | Game | `com.tcgtrader.entity.Game` | `games` |
| 5 | GameSet | `com.tcgtrader.entity.GameSet` | `sets` |
| 6 | Item (supertipo) | `com.tcgtrader.entity.Item` | `items` |
| 7 | Card | `com.tcgtrader.entity.Card` | `cards` |
| 8 | Cart | `com.tcgtrader.entity.Cart` | `carts` |
| 9 | CartItem | `com.tcgtrader.entity.CartItem` | `cart_items` |
| 10 | Order | `com.tcgtrader.entity.Order` | `orders` |
| 11 | OrderItem | `com.tcgtrader.entity.OrderItem` | `order_items` |
| 12 | Payment | `com.tcgtrader.entity.Payment` | `payments` |
| 13 | Deck | `com.tcgtrader.entity.Deck` | `decks` |
| 14 | DeckCard (con `DeckCardId` @Embeddable) | `com.tcgtrader.entity.DeckCard` | `deck_cards` |
| 15 | Discount | `com.tcgtrader.entity.Discount` | `discounts` |
| 16 | StockMovement | `com.tcgtrader.entity.StockMovement` | `stock_movements` |

---

## 3. Tabla de endpoints

Prefijo global: `/api`. Endpoints públicos: `/api/auth/**`, Swagger, y `GET` sobre `/api/cards/**`, `/api/decks/**`, `/api/games/**`, `/api/sets/**`, `/api/items/**` (`SecurityConfig.java`).

| Método | URL | DTO / Entidad | Auth | Rol |
|---|---|---|---|---|
| POST | `/api/auth/login` | `LoginRequest` → `AuthResponse` | NO | — |
| POST | `/api/auth/register` | `UserRequest` → `AuthResponse` | NO | — |
| POST | `/api/users` | `UserRequest` → `UserResponse` | SÍ | ADMIN |
| GET | `/api/users/{id}` | `UserResponse` | SÍ | ADMIN o dueño |
| DELETE | `/api/users/{id}` | — | SÍ | ADMIN |
| POST | `/api/roles` | `RoleRequest` → `RoleResponse` | SÍ | ADMIN |
| GET | `/api/roles` | `List<RoleResponse>` | SÍ | ADMIN |
| GET | `/api/roles/{id}` | `RoleResponse` | SÍ | ADMIN |
| POST | `/api/cards` | `CardRequest` → `CardResponse` | SÍ | ADMIN |
| GET | `/api/cards` | `List<CardResponse>` | NO | — |
| GET | `/api/cards/{id}` | `CardResponse` | NO | — |
| PUT | `/api/cards/{id}` | `CardRequest` → `CardResponse` | SÍ | ADMIN |
| DELETE | `/api/cards/{id}` | — | SÍ | ADMIN |
| POST | `/api/games` | `GameRequest` → `GameResponse` | SÍ | ADMIN |
| GET | `/api/games` | `List<GameResponse>` | NO | — |
| GET | `/api/games/{id}` | `GameResponse` | NO | — |
| POST | `/api/sets` | `SetRequest` → `SetResponse` | SÍ | ADMIN |
| GET | `/api/sets` | `List<SetResponse>` | NO | — |
| GET | `/api/sets/{id}` | `SetResponse` | NO | — |
| GET | `/api/games/{gameId}/sets` | `List<SetResponse>` | NO | — |
| POST | `/api/decks` | `DeckRequest` → `DeckResponse` | SÍ | ADMIN |
| GET | `/api/decks` | `List<DeckResponse>` | NO | — |
| GET | `/api/decks/{id}` | `DeckResponse` | NO | — |
| PUT | `/api/decks/{id}` | `DeckRequest` → `DeckResponse` | SÍ | ADMIN |
| DELETE | `/api/decks/{id}` | — | SÍ | ADMIN |
| POST | `/api/discounts` | `DiscountRequest` → `DiscountResponse` | SÍ | ADMIN |
| GET | `/api/items/{itemId}/discounts` | `List<DiscountResponse>` | SÍ | USER/ADMIN |
| GET | `/api/items/{itemId}/discounts/active` | `List<DiscountResponse>` | SÍ | USER/ADMIN |
| GET | `/api/users/{userId}/cart` | `CartResponse` | SÍ | ADMIN o dueño |
| POST | `/api/users/{userId}/cart/items` | `CartItemRequest` → `CartResponse` | SÍ | ADMIN o dueño |
| PUT | `/api/users/{userId}/cart/items/{itemId}` | `CartItemRequest` → `CartResponse` | SÍ | ADMIN o dueño |
| DELETE | `/api/users/{userId}/cart/items/{itemId}` | `CartResponse` | SÍ | ADMIN o dueño |
| DELETE | `/api/users/{userId}/cart` | — | SÍ | ADMIN o dueño |
| POST | `/api/users/{userId}/checkout` | `CheckoutRequest` → `OrderResponse` | SÍ | ADMIN o dueño |
| GET | `/api/users/{userId}/orders` | `List<OrderResponse>` | SÍ | ADMIN o dueño |
| GET | `/api/orders/{id}` | `OrderResponse` | SÍ | ADMIN o dueño |
| GET | `/api/admin/stock-movements` | `List<StockMovementResponse>` | SÍ | ADMIN |
| GET | `/api/admin/orders` | `List<OrderResponse>` | SÍ | ADMIN |

**Roles implementados:** `ROLE_ADMIN`, `ROLE_USER`. Enforced vía `@EnableMethodSecurity` + `@PreAuthorize` a nivel método/clase.

---

## 4. Evidencias (capturas a adjuntar)

A completar con screenshots (una por sección, total ≤ 1 página):

1. **Workbench / pgAdmin** — tablas `roles`, `users`, `cards` con datos.
2. **Login** — `POST /api/auth/login` respondiendo `{ token: "..." }`.
3. **Endpoint protegido OK** — `GET /api/users/{id}` con header `Authorization: Bearer <token>` → 200.
4. **Sin token** — mismo request sin header → 401.
5. **Rol insuficiente** — `POST /api/cards` con token de USER → 403.

Guardar PNG/JPG en `evidencias/` y referenciarlas desde el PDF.

---

## 5. Repositorio + pasos de ejecución

**Repo:** https://github.com/<org>/TPO--TCG-CardHeaven-Grupo3

```bash
git clone <url> && cd TPO--TCG-CardHeaven-Grupo3
cp src/main/resources/application.properties.example src/main/resources/application.properties
docker compose up -d          # levanta Postgres 16 en :5432
./mvnw spring-boot:run        # backend en :8080
```

**Usuarios semilla** (a cargar vía `POST /api/auth/register` o data-seed):
- admin / admin123 (ROLE_ADMIN)
- user  / user123  (ROLE_USER)

Swagger: http://localhost:8080/swagger-ui.html

---

## Brechas a cerrar antes de entregar

- [ ] **DB**: enunciado dice MySQL, proyecto usa PostgreSQL. Confirmar con cátedra o migrar.
- [ ] Crear `application.properties.example` sin secretos.
- [ ] Exportar colección Postman/Insomnia.
- [ ] Data-seed (admin + user + cartas) para que las capturas tengan contenido.
- [ ] Completar portada (legajos) y URL final del repo público.
