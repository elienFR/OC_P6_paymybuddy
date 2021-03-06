-- Create database
CREATE DATABASE IF NOT EXISTS paymybuddy;

-- Use paymybuddy database
USE paymybuddy;

-- Dropping all tables first
DROP TABLE IF exists paymybuddy.account_credits;
DROP TABLE IF EXISTS paymybuddy.transactions;
DROP TABLE IF EXISTS paymybuddy.bank_transactions;
DROP TABLE IF EXISTS paymybuddy.user_authority;
DROP TABLE IF EXISTS paymybuddy.user_beneficiaries;
DROP TABLE IF EXISTS paymybuddy.accounts;
DROP TABLE IF EXISTS paymybuddy.authorities;
DROP TABLE IF EXISTS paymybuddy.users;


-- paymybuddy.authorities definition

CREATE TABLE `authorities` (
  `authority_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`authority_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddy.users definition

CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `last_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` binary(60) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `github_user` binary(64) DEFAULT NULL,
  `google_user` binary(64) DEFAULT NULL,
  `local_user` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- paymybuddy.accounts definition

CREATE TABLE `accounts` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `currency_iso` varchar(3) NOT NULL,
  `balance` decimal(10,3) NOT NULL DEFAULT '0.000',
  `user_id` int NOT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `accounts_un` (`user_id`),
  KEY `account_FK` (`user_id`),
  CONSTRAINT `account_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddy.bank_transactions definition

CREATE TABLE `bank_transactions` (
  `bank_transaction_id` int NOT NULL AUTO_INCREMENT,
  `amount` float NOT NULL DEFAULT '0',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `paymybuddy_account` int NOT NULL,
  `exterior_iban` varchar(34) NOT NULL DEFAULT '0',
  `exterior_BIC` varchar(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bank_transaction_id`),
  KEY `bank_transaction_FK` (`paymybuddy_account`),
  CONSTRAINT `bank_transaction_FK` FOREIGN KEY (`paymybuddy_account`) REFERENCES `accounts` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- paymybuddy.account_credits definition

CREATE TABLE `account_credits` (
  `account_credits_id` int NOT NULL AUTO_INCREMENT,
  `amount` float NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `paymybuddy_account` int NOT NULL,
  `credit_card_number` varchar(20) NOT NULL,
  `crypto` varchar(5) NOT NULL,
  `expiration_date` varchar(4) NOT NULL,
  PRIMARY KEY (`account_credits_id`),
  KEY `account_credits_FK` (`paymybuddy_account`),
  CONSTRAINT `account_credits_FK` FOREIGN KEY (`paymybuddy_account`) REFERENCES `accounts` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- paymybuddy.transactions definition

CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `amount` float NOT NULL DEFAULT '0',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `from_account` int NOT NULL,
  `to_account` int NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `transaction_FK` (`from_account`),
  KEY `transaction_FK_1` (`to_account`),
  CONSTRAINT `transaction_FK` FOREIGN KEY (`from_account`) REFERENCES `accounts` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `transaction_FK_1` FOREIGN KEY (`to_account`) REFERENCES `accounts` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddy.user_authority definition

CREATE TABLE `user_authority` (
  `user_authority_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `authority_id` int NOT NULL,
  PRIMARY KEY (`user_authority_id`),
  KEY `user_authority_FK` (`user_id`),
  KEY `user_authority_FK_1` (`authority_id`),
  CONSTRAINT `user_authority_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_authority_FK_1` FOREIGN KEY (`authority_id`) REFERENCES `authorities` (`authority_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddy.user_beneficiaries definition

CREATE TABLE `user_beneficiaries` (
  `user_beneficiary_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `beneficiary_id` int NOT NULL,
  PRIMARY KEY (`user_beneficiary_id`),
  UNIQUE KEY `user_beneficiaries_un` (`user_id`,`beneficiary_id`),
  KEY `user_beneficiaries_FK` (`user_id`),
  KEY `user_beneficiaries_FK_1` (`beneficiary_id`),
  CONSTRAINT `user_beneficiaries_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_beneficiaries_FK_1` FOREIGN KEY (`beneficiary_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO paymybuddy.users (first_name,last_name,email,password,enabled,github_user,google_user,local_user) VALUES
	 ('admin',NULL,'admin@email.com',0x243279243130244B432F76762F2E4B67536C51326D676B6538674A662E36716B4F596641546F796B3535715057366331776E63773247427151657569,1,NULL,NULL,1),
	 ('paymybuddy','fees','paymybuddyfees@email.com',NULL,1,NULL,NULL,1);

INSERT INTO paymybuddy.authorities (name) VALUES
	 ('ROLE_ADMIN'),
	 ('ROLE_USER');

INSERT INTO paymybuddy.user_authority (user_id,authority_id) VALUES
	 (1,1),
	 (1,2),
	 (2,2);

INSERT INTO paymybuddy.accounts (currency_iso,balance,user_id) VALUES
	 ('EUR',1231330.545,1),
	 ('EUR',0,2);