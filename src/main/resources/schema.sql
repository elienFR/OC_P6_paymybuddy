-- Dropping all tables first
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS bank_transactions;
DROP TABLE IF EXISTS user_authority;
DROP TABLE IF EXISTS user_beneficiaries;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;


-- paymybuddytest.authorities definition

CREATE TABLE `authorities` (
  `authority_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`authority_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddytest.users definition

CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `last_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` binary(60) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `github_user` binary(64) DEFAULT NULL,
  `google_user` binary(64) DEFAULT NULL,
  `local_user` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- paymybuddytest.accounts definition

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


-- paymybuddytest.bank_transactions definition

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


-- paymybuddytest.transactions definition

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


-- paymybuddytest.user_authority definition

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


-- paymybuddytest.user_beneficiaries definition

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