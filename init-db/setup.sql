CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    due_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDENTE'
);

-- Dados de exemplo [cite: 49]
INSERT INTO users (username, password) VALUES ('admin', 'admin123');
INSERT INTO tasks (title, description, status) VALUES ('Primeira Tarefa', 'Finalizar o desafio técnico', 'EM_ANDAMENTO');
