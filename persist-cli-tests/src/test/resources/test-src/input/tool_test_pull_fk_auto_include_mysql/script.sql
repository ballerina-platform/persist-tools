-- Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
--
-- WSO2 LLC. licenses this file to you under the Apache License,
-- Version 2.0 (the "License"); you may not use this file except
-- in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.

DROP DATABASE IF EXISTS persist;
CREATE DATABASE persist;
USE persist;

-- Albums table (referenced by both album_ratings and purchases)
CREATE TABLE albums (
  album_id INT AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  artist VARCHAR(100) NOT NULL,
  price DECIMAL(6,2) NOT NULL,
  stock INT NOT NULL,
  PRIMARY KEY(album_id)
);

-- Album ratings table (references albums)
CREATE TABLE album_ratings (
  customer_name VARCHAR(100) NOT NULL,
  rating INT,
  review VARCHAR(255),
  rated_on TIMESTAMP,
  album_id INT NOT NULL,
  FOREIGN KEY(album_id) REFERENCES albums(album_id),
  PRIMARY KEY(album_id, customer_name)
);

-- Purchases table (references albums)
CREATE TABLE purchases (
  purchase_id INT AUTO_INCREMENT,
  customer_name VARCHAR(100) NOT NULL,
  quantity INT NOT NULL,
  total_price DECIMAL(8,2) NOT NULL,
  purchase_time TIMESTAMP,
  album_id INT NOT NULL,
  FOREIGN KEY(album_id) REFERENCES albums(album_id),
  PRIMARY KEY(purchase_id)
);

CREATE INDEX album_id ON purchases (album_id);
