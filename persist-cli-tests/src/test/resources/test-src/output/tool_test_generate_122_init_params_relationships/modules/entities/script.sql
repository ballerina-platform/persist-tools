-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `OrderItem`;
DROP TABLE IF EXISTS `Order`;
DROP TABLE IF EXISTS `Product`;
DROP TABLE IF EXISTS `Customer`;

CREATE TABLE `Customer` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`email` VARCHAR(191) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Product` (
	`id` INT NOT NULL,
	`name` VARCHAR(191) NOT NULL,
	`description` VARCHAR(191) NOT NULL,
	`price` DECIMAL(65,30) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Order` (
	`id` INT NOT NULL,
	`orderNumber` VARCHAR(191) NOT NULL,
	`totalAmount` DECIMAL(65,30) NOT NULL,
	`customerId` INT NOT NULL,
	FOREIGN KEY(`customerId`) REFERENCES `Customer`(`id`),
	PRIMARY KEY(`id`)
);

CREATE TABLE `OrderItem` (
	`id` INT NOT NULL,
	`quantity` INT NOT NULL,
	`price` DECIMAL(65,30) NOT NULL,
	`orderId` INT NOT NULL,
	FOREIGN KEY(`orderId`) REFERENCES `Order`(`id`),
	`productId` INT NOT NULL,
	FOREIGN KEY(`productId`) REFERENCES `Product`(`id`),
	PRIMARY KEY(`id`)
);


