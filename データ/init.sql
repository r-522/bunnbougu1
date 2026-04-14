PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS shipments;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS inventories;
DROP TABLE IF EXISTS products;

CREATE TABLE products (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  code TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL,
  category TEXT NOT NULL,
  unit_price INTEGER NOT NULL
);

CREATE TABLE inventories (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  product_id INTEGER NOT NULL,
  quantity INTEGER NOT NULL,
  location TEXT NOT NULL,
  FOREIGN KEY(product_id) REFERENCES products(id)
);

CREATE TABLE orders (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  product_id INTEGER NOT NULL,
  quantity INTEGER NOT NULL,
  customer_name TEXT NOT NULL,
  order_date TEXT NOT NULL,
  status TEXT NOT NULL,
  FOREIGN KEY(product_id) REFERENCES products(id)
);

CREATE TABLE shipments (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  shipped_quantity INTEGER NOT NULL,
  shipped_date TEXT NOT NULL,
  carrier TEXT NOT NULL,
  FOREIGN KEY(order_id) REFERENCES orders(id),
  FOREIGN KEY(product_id) REFERENCES products(id)
);

INSERT INTO products(code, name, category, unit_price) VALUES
('P-100', 'ノート A5', '紙製品', 180),
('P-200', 'ボールペン 黒', '筆記具', 120);

INSERT INTO inventories(product_id, quantity, location) VALUES
(1, 120, 'A-01'),
(2, 200, 'B-03');
