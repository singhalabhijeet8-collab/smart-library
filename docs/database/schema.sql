CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    author VARCHAR(255),
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS issue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    issue_date DATE,
    due_date DATE,
    returned BOOLEAN DEFAULT FALSE,
    return_date DATE,
    CONSTRAINT fk_issue_book FOREIGN KEY (book_id) REFERENCES book(id),
    CONSTRAINT fk_issue_user FOREIGN KEY (user_id) REFERENCES user(id)
);

INSERT INTO book (title, author, available) VALUES
('The Alchemist', 'Paulo Coelho', TRUE),
('Atomic Habits', 'James Clear', TRUE),
('Clean Code', 'Robert C. Martin', TRUE);
