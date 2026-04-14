-- 在庫・受注・出荷の最小構成サンプル
-- 参照整合性を重視し、NOT NULL / UNIQUE / FOREIGN KEY を明示しています。

BEGIN;

-- =========================
-- 1) 商品マスタ
-- =========================
CREATE TABLE IF NOT EXISTS products (
    product_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku             VARCHAR(40) NOT NULL,
    product_name    VARCHAR(100) NOT NULL,
    unit            VARCHAR(20) NOT NULL DEFAULT 'pcs',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_products_sku UNIQUE (sku)
);

-- =========================
-- 2) 在庫
-- =========================
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id      BIGINT NOT NULL,
    quantity_on_hand INTEGER NOT NULL DEFAULT 0,
    reorder_point   INTEGER NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_inventory_product UNIQUE (product_id),
    CONSTRAINT chk_inventory_qty_nonnegative CHECK (quantity_on_hand >= 0),
    CONSTRAINT chk_inventory_reorder_nonnegative CHECK (reorder_point >= 0),
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================
-- 3) 取引先
-- =========================
CREATE TABLE IF NOT EXISTS customers (
    customer_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_code   VARCHAR(30) NOT NULL,
    customer_name   VARCHAR(120) NOT NULL,
    contact_name    VARCHAR(80),
    phone           VARCHAR(30),
    email           VARCHAR(255),
    address         VARCHAR(255),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_customers_code UNIQUE (customer_code),
    CONSTRAINT uq_customers_email UNIQUE (email)
);

-- =========================
-- 4) 受注（ヘッダ / 明細）
-- =========================
CREATE TABLE IF NOT EXISTS orders (
    order_id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_no        VARCHAR(30) NOT NULL,
    customer_id     BIGINT NOT NULL,
    order_date      DATE NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'NEW',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_orders_order_no UNIQUE (order_no),
    CONSTRAINT chk_orders_status CHECK (status IN ('NEW', 'CONFIRMED', 'SHIPPED', 'CANCELLED')),
    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (customer_id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id        BIGINT NOT NULL,
    line_no         INTEGER NOT NULL,
    product_id      BIGINT NOT NULL,
    quantity        INTEGER NOT NULL,
    unit_price      NUMERIC(12,2) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_order_items_line UNIQUE (order_id, line_no),
    CONSTRAINT chk_order_items_qty_positive CHECK (quantity > 0),
    CONSTRAINT chk_order_items_price_nonnegative CHECK (unit_price >= 0),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders (order_id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================
-- 5) 出荷（ヘッダ / 明細）
-- =========================
CREATE TABLE IF NOT EXISTS shipments (
    shipment_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shipment_no     VARCHAR(30) NOT NULL,
    order_id        BIGINT NOT NULL,
    shipped_date    DATE,
    status          VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_shipments_no UNIQUE (shipment_no),
    CONSTRAINT chk_shipments_status CHECK (status IN ('PLANNED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT fk_shipments_order
        FOREIGN KEY (order_id)
        REFERENCES orders (order_id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS shipment_items (
    shipment_item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shipment_id      BIGINT NOT NULL,
    line_no          INTEGER NOT NULL,
    product_id       BIGINT NOT NULL,
    quantity         INTEGER NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_shipment_items_line UNIQUE (shipment_id, line_no),
    CONSTRAINT chk_shipment_items_qty_positive CHECK (quantity > 0),
    CONSTRAINT fk_shipment_items_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES shipments (shipment_id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,
    CONSTRAINT fk_shipment_items_product
        FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

-- =========================
-- 6) スタッフ簡易ログイン（メール/パスワードなし）
-- =========================
CREATE TABLE IF NOT EXISTS staff_sessions (
    session_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_code      VARCHAR(30) NOT NULL,
    display_name    VARCHAR(80) NOT NULL,
    login_token     VARCHAR(128) NOT NULL,
    issued_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP NOT NULL,
    last_seen_at    TIMESTAMP,
    is_revoked      BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_staff_sessions_token UNIQUE (login_token),
    CONSTRAINT chk_staff_sessions_exp CHECK (expires_at > issued_at)
);

-- =========================
-- 初期データ（商品 + 在庫）
-- =========================
INSERT INTO products (sku, product_name, unit)
VALUES
    ('NOTE-A5-001', 'A5ノート', '冊'),
    ('PEN-BLK-001', 'ボールペン（黒）', '本'),
    ('ERASER-001',   '消しゴム', '個'),
    ('RULER-150',    '定規 15cm', '本'),
    ('FILE-A4-001',  'A4クリアファイル', '枚');

INSERT INTO inventory (product_id, quantity_on_hand, reorder_point)
SELECT p.product_id,
       CASE p.sku
           WHEN 'NOTE-A5-001' THEN 120
           WHEN 'PEN-BLK-001' THEN 300
           WHEN 'ERASER-001' THEN 200
           WHEN 'RULER-150' THEN 80
           WHEN 'FILE-A4-001' THEN 150
           ELSE 0
       END AS quantity_on_hand,
       CASE p.sku
           WHEN 'NOTE-A5-001' THEN 30
           WHEN 'PEN-BLK-001' THEN 100
           WHEN 'ERASER-001' THEN 60
           WHEN 'RULER-150' THEN 20
           WHEN 'FILE-A4-001' THEN 40
           ELSE 0
       END AS reorder_point
FROM products p
WHERE p.sku IN ('NOTE-A5-001', 'PEN-BLK-001', 'ERASER-001', 'RULER-150', 'FILE-A4-001');

COMMIT;
