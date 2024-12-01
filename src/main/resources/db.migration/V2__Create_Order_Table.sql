ALTER TABLE customer ALTER COLUMN customer_id TYPE BIGINT;


CREATE TABLE IF NOT EXISTS CustomerOrder(
    order_id BIGSERIAL PRIMARY KEY,
    order_items JSONB NOT NULL,
    order_status VARCHAR (60) NOT NULL,
    order_total_price DECIMAL(10, 2) NOT NULL,
    order_creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    customer_id BIGINT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

CREATE TABLE IF NOT EXISTS CustomerBasket(
    basket_id BIGSERIAL PRIMARY KEY,
    items_in_basket JSONB NOT NULL,
    basket_creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP,
    basket_total_price DECIMAL(10,2) NOT NULL,
    customer_id BIGINT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

CREATE TABLE IF NOT EXISTS Product(
    product_id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_price DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS ProductStock(
   product_stock_id BIGSERIAL PRIMARY KEY,
   product_id BIGINT NOT NULL,
   quantity INT NOT NULL,
   FOREIGN KEY (product_id) REFERENCES Product(product_id)
);
