-- AUTO-GENERATED FILE.

-- This file is an auto-generated file by Ballerina persistence layer for model.
-- Please verify the generated scripts and execute them against the target DB server.

DROP TABLE IF EXISTS `Payment`;
DROP TABLE IF EXISTS `OrderItem`;
DROP TABLE IF EXISTS `Order`;
DROP TABLE IF EXISTS `Book`;

CREATE TABLE `Book` (
	`bookId` VARCHAR(191) NOT NULL,
	`title` VARCHAR(191) NOT NULL,
	`author` VARCHAR(191) NOT NULL,
	`price` DECIMAL(65,30) NOT NULL,
	`stock` INT NOT NULL,
	PRIMARY KEY(`bookId`)
);

CREATE TABLE `Order` (
	`orderId` VARCHAR(191) NOT NULL,
	`customerId` VARCHAR(191) NOT NULL,
	`createdAt` VARCHAR(191) NOT NULL,
	`totalPrice` DECIMAL(65,30) NOT NULL,
	PRIMARY KEY(`orderId`)
);

CREATE TABLE `OrderItem` (
	`orderItemId` VARCHAR(191) NOT NULL,
	`quantity` INT NOT NULL,
	`price` DECIMAL(65,30) NOT NULL,
	`orderitemBookId` VARCHAR(191) UNIQUE NOT NULL,
	FOREIGN KEY(`orderitemBookId`) REFERENCES `Book`(`bookId`),
	`orderOrderId` VARCHAR(191) NOT NULL,
	FOREIGN KEY(`orderOrderId`) REFERENCES `Order`(`orderId`),
	PRIMARY KEY(`orderItemId`)
);

CREATE TABLE `Payment` (
	`paymentId` VARCHAR(191) NOT NULL,
	`paymentAmount` DECIMAL(65,30) NOT NULL,
	`paymentDate` VARCHAR(191) NOT NULL,
	`paymentOrderId` VARCHAR(191) UNIQUE NOT NULL,
	FOREIGN KEY(`paymentOrderId`) REFERENCES `Order`(`orderId`),
	PRIMARY KEY(`paymentId`)
);
