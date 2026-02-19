DROP DATABASE IF EXISTS persist_root;
CREATE DATABASE persist_root;
USE persist_root;

CREATE TABLE User (
  id INT,
  name VARCHAR(191) NOT NULL,
  PRIMARY KEY (id)
);

DROP DATABASE IF EXISTS persist_store;
CREATE DATABASE persist_store;
USE persist_store;

CREATE TABLE Product (
  id INT,
  name VARCHAR(191) NOT NULL,
  PRIMARY KEY (id)
);
