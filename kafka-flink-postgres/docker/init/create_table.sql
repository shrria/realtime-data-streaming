CREATE TABLE weather (
    id serial PRIMARY KEY,
    city VARCHAR(255) NOT NULL,
    average_temperature DOUBLE PRECISION NOT NULL
);