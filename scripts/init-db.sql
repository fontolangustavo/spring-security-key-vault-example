CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    price NUMERIC NOT NULL
);

INSERT INTO products (name, price) VALUES
('Produto 1', 10.00),
('Produto 2', 20.00),
('Produto 3', 30.00),
('Produto 4', 40.00),
('Produto 5', 50.00)
ON CONFLICT (name) DO NOTHING;
