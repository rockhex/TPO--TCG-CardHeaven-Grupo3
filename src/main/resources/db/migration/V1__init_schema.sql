CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE addresses (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    street     VARCHAR(255) NOT NULL,
    city       VARCHAR(255) NOT NULL,
    country    VARCHAR(255) NOT NULL,
    zip_code   VARCHAR(20),
    is_default BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE cards (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name      VARCHAR(255) NOT NULL,
    set_code  VARCHAR(50),
    rarity    VARCHAR(50),
    condition VARCHAR(50),
    price     NUMERIC(10,2) NOT NULL,
    stock     INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    image_url TEXT
);

CREATE TABLE carts (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE cart_items (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id    UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    card_id    UUID NOT NULL REFERENCES cards(id),
    quantity   INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE orders (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id),
    address_id   UUID NOT NULL REFERENCES addresses(id),
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount NUMERIC(10,2) NOT NULL,
    placed_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE order_items (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id   UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    card_id    UUID NOT NULL REFERENCES cards(id),
    quantity   INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10,2) NOT NULL
);

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
