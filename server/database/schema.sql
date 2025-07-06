CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(20) NOT NULL CHECK (role IN ('patient', 'dentist', 'secretary')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
); 