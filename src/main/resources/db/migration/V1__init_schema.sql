CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================
-- ROLES
-- =====================
CREATE TABLE roles (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

-- =====================
-- USERS
-- =====================
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id       UUID NOT NULL REFERENCES roles(id),
    email         VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_login    TIMESTAMPTZ
);

-- =====================
-- ADDRESSES
-- =====================
CREATE TABLE addresses (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    street   VARCHAR(255) NOT NULL,
    city     VARCHAR(255) NOT NULL,
    country  VARCHAR(255) NOT NULL,
    zip_code VARCHAR(20)
);

-- =====================
-- GAMES
-- =====================
CREATE TABLE games (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL
);

-- =====================
-- SETS
-- =====================
CREATE TABLE sets (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    game_id UUID NOT NULL REFERENCES games(id),
    name    VARCHAR(255) NOT NULL,
    code    VARCHAR(50) NOT NULL
);

-- =====================
-- ITEMS (supertype)
-- =====================
CREATE TABLE items (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL
);

-- =====================
-- CARDS
-- =====================
CREATE TABLE cards (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id   UUID NOT NULL UNIQUE REFERENCES items(id) ON DELETE CASCADE,
    set_id    UUID NOT NULL REFERENCES sets(id),
    name      VARCHAR(255) NOT NULL,
    rarity    VARCHAR(50),
    condition VARCHAR(50),
    price     NUMERIC(10,2) NOT NULL,
    stock     INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    image_url TEXT
);

-- =====================
-- DECKS
-- =====================
CREATE TABLE decks (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id     UUID NOT NULL UNIQUE REFERENCES items(id) ON DELETE CASCADE,
    set_id      UUID NOT NULL REFERENCES sets(id),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    price       NUMERIC(10,2) NOT NULL,
    stock       INT NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

-- =====================
-- DECK_CARDS
-- =====================
CREATE TABLE deck_cards (
    deck_id  UUID NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
    card_id  UUID NOT NULL REFERENCES cards(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (deck_id, card_id)
);

-- =====================
-- DISCOUNTS
-- =====================
CREATE TABLE discounts (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id    UUID NOT NULL REFERENCES items(id),
    percentage NUMERIC(5,2) NOT NULL,
    valid_from TIMESTAMPTZ NOT NULL,
    valid_to   TIMESTAMPTZ NOT NULL,
    active     BOOLEAN NOT NULL DEFAULT true
);

-- =====================
-- CARTS
-- =====================
CREATE TABLE carts (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

-- =====================
-- CART_ITEMS
-- =====================
CREATE TABLE cart_items (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id  UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    item_id  UUID NOT NULL REFERENCES items(id),
    quantity INT NOT NULL CHECK (quantity > 0)
);

-- =====================
-- ORDERS
-- =====================
CREATE TABLE orders (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id),
    address_id   UUID NOT NULL REFERENCES addresses(id),
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount NUMERIC(10,2) NOT NULL,
    placed_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =====================
-- ORDER_ITEMS
-- =====================
CREATE TABLE order_items (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id   UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    item_id    UUID NOT NULL REFERENCES items(id),
    quantity   INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10,2) NOT NULL
);

-- =====================
-- PAYMENTS
-- =====================
CREATE TABLE payments (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id              UUID NOT NULL UNIQUE REFERENCES orders(id),
    external_processor_id VARCHAR(255),
    provider              VARCHAR(100) NOT NULL,
    status                VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount                NUMERIC(10,2) NOT NULL,
    currency              VARCHAR(10) NOT NULL DEFAULT 'USD',
    processed_at          TIMESTAMPTZ
);
