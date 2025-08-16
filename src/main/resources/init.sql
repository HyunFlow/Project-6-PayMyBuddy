CREATE DATABASE IF NOT EXISTS PayMyBuddy
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE PayMyBuddy;

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS user_relations;
DROP TABLE IF EXISTS external_accounts;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255)           NOT NULL,
    email    VARCHAR(255)           NOT NULL,
    password VARCHAR(255)           NOT NULL,
    role     ENUM ('ADMIN', 'USER') NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE accounts
(
    account_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT            NOT NULL,
    balance        DECIMAL(10, 2) NOT NULL                DEFAULT 0.00,
    account_type   ENUM ('SAVING', 'CHECKING')            DEFAULT 'CHECKING',
    account_status ENUM ('ACTIVE', 'CLOSED', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at     DATETIME                               DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_accounts_user_id (user_id)
) ENGINE = InnoDB;

CREATE TABLE external_accounts
(
    external_account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id             INT          NOT NULL,
    bank_name           VARCHAR(50)  NOT NULL,
    account_number      VARCHAR(255) NOT NULL,
    account_holder_name VARCHAR(255) NOT NULL,
    registered_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_ext_accounts_number UNIQUE (account_number),
    CONSTRAINT fk_ext_accounts_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_ext_accounts_user_id (user_id)
) ENGINE = InnoDB;

CREATE TABLE user_relations
(
    relation_id      INT AUTO_INCREMENT PRIMARY KEY,
    defining_user_id INT          NOT NULL,
    related_user_id  INT          NOT NULL,
    relation_name    VARCHAR(255) NULL,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_relations UNIQUE (defining_user_id, related_user_id),
    CONSTRAINT fk_relations_def_user FOREIGN KEY (defining_user_id) REFERENCES users (id),
    CONSTRAINT fk_relations_rel_user FOREIGN KEY (related_user_id) REFERENCES users (id),
    INDEX idx_user_relations_def_user (defining_user_id),
    INDEX idx_user_relations_rel_user (related_user_id)
) ENGINE = InnoDB;

CREATE TABLE transactions
(
    transaction_id               INT AUTO_INCREMENT PRIMARY KEY,
    sender_account_id            INT                                            NULL,
    sender_external_account_id   INT                                            NULL,
    receiver_account_id          INT                                            NULL,
    receiver_external_account_id INT                                            NULL,
    amount                       DECIMAL(10, 2)                                 NOT NULL,
    transaction_fee              DECIMAL(10, 2)                                 NOT NULL DEFAULT 0.00,
    currency                     ENUM ('EUR','USD')                             NOT NULL DEFAULT 'EUR',
    transaction_type             ENUM ('CHARGE','TRANSFER')                     NOT NULL,
    transaction_status           ENUM ('PENDING','SUCCESS','FAILED','REJECTED') NOT NULL,
    transaction_time             DATETIME                                                DEFAULT CURRENT_TIMESTAMP,
    external_bank_info           VARCHAR(255)                                   NULL,
    description                  VARCHAR(255)                                   NULL,
    CONSTRAINT fk_tx_sender_acc FOREIGN KEY (sender_account_id) REFERENCES accounts (account_id),
    CONSTRAINT fk_tx_sender_ext FOREIGN KEY (sender_external_account_id) REFERENCES external_accounts (external_account_id),
    CONSTRAINT fk_tx_receiver_acc FOREIGN KEY (receiver_account_id) REFERENCES accounts (account_id),
    CONSTRAINT fk_tx_receiver_ext FOREIGN KEY (receiver_external_account_id) REFERENCES external_accounts (external_account_id),
    INDEX idx_transactions_sender_acc (sender_account_id),
    INDEX idx_transactions_receiver_acc (receiver_account_id)
) ENGINE = InnoDB;