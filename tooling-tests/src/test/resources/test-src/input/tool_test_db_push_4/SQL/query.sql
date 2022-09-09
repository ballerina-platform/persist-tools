DROP TABLE IF EXISTS Medical_Need;

DROP TABLE IF EXISTS Item;

CREATE TABLE Item (
                      id INT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(191) NOT NULL,
                      PRIMARY KEY(id)
)  AUTO_INCREMENT = 15;

CREATE TABLE Medical_Need (
                              needId INT NOT NULL AUTO_INCREMENT,
                              beneficiaryId INT NOT NULL,
                              period VARCHAR(191),
                              urgency VARCHAR(191),
                              quantity INT NOT NULL,
                              itemId INT,
                              CONSTRAINT FK_MEDICAL_NEED_ITEM_0 FOREIGN KEY(itemId) REFERENCES Item(id) ON DELETE CASCADE,
                              PRIMARY KEY(needId),
                              UNIQUE KEY(needId)
)  AUTO_INCREMENT = 12;