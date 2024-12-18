CREATE DATABASE orders_manager

CREATE TABLE IF NOT EXISTS customer
( customer_id BIGSERIAL PRIMARY KEY,
  first_name varchar(50),
  last_name varchar(50),
  email varchar(100) unique,
  address varchar(100)
)
