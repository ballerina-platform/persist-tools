DROP TABLE IF EXISTS MultipleAssociations;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Dept;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS Profile;

CREATE TABLE Profile (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	isAdult BOOLEAN NOT NULL,
	salary FLOAT NOT NULL,
	age DECIMAL NOT NULL,
	isRegistered BINARY NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE Student (
	id INT NOT NULL,
	firstName VARCHAR(191) NOT NULL,
	age INT NOT NULL,
	lastName VARCHAR(191) NOT NULL,
	nicNo VARCHAR(191) NOT NULL,
	PRIMARY KEY(id,firstName)
);

CREATE TABLE Customer (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	age INT NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE Dept (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE User (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	profileId INT,
	CONSTRAINT FK_USER_PROFILE_0 FOREIGN KEY(profileId) REFERENCES Profile(id),
	PRIMARY KEY(id)
);

CREATE TABLE MultipleAssociations (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	userId INT,
	CONSTRAINT FK_MULTIPLEASSOCIATIONS_USER_0 FOREIGN KEY(userId) REFERENCES User(id),
	deptId INT,
	CONSTRAINT FK_MULTIPLEASSOCIATIONS_DEPT_0 FOREIGN KEY(deptId) REFERENCES Dept(id),
	customerId INT,
	CONSTRAINT FK_MULTIPLEASSOCIATIONS_CUSTOMER_0 FOREIGN KEY(customerId) REFERENCES Customer(id),
	PRIMARY KEY(id)
);