DROP DATABASE IF EXISTS PayMyBuddy;
CREATE DATABASE IF NOT EXISTS PayMyBuddy CHARSET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE PayMyBuddy;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
	id	INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email	VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL
);

DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts (
	account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    balance DECIMAL(10,2) NOT NULL,
    account_type ENUM('SAVING', 'CHECKING') NULL,
	account_status ENUM('ACTIVE', 'CLOSED', 'SUSPENDED') NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY(user_id) REFERENCES users(id)
);

DROP TABLE IF EXISTS external_accounts;

CREATE TABLE external_accounts (
	external_account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    bank_name VARCHAR(50) NOT NULL,
    account_number VARCHAR(255) UNIQUE NOT NULL,
    account_holder_name VARCHAR(255) NOT NULL,
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY(user_id) REFERENCES users(id)
);

DROP TABLE IF EXISTS user_relations;

CREATE TABLE user_relations (
	relation_id INT AUTO_INCREMENT PRIMARY KEY,
    defining_user_id INT NOT NULL,
    related_user_id INT NOT NULL,
    relation_name VARCHAR(255) NOT NULL,
    created_at DATETIME,

    UNIQUE(defining_user_id, related_user_id),
    FOREIGN KEY(defining_user_id) REFERENCES users(id),
    FOREIGN KEY(related_user_id) REFERENCES users(id)
);

DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
	transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_account_id INT NULL,
    sender_external_account_id INT NULL,
    receiver_account_id INT NULL,
    receiver_external_account_id INT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_fee DECIMAL(10,2) NOT NULL,
    currency VARCHAR(20) NOT NULL,
    transaction_type ENUM('CHARGE', 'TRANSFER') NOT NULL,
    transaction_status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REJECTED') NOT NULL,
    transaction_time DATETIME,
    external_bank_info VARCHAR(255) NULL,
    description VARCHAR(255) NULL,

    FOREIGN KEY (sender_account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (sender_external_account_id) REFERENCES external_accounts(external_account_id),
    FOREIGN KEY (receiver_account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (receiver_external_account_id) REFERENCES external_accounts(external_account_id)
);