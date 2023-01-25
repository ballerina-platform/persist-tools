DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Profile;

CREATE TABLE Profile (
	id INT NOT NULL,
	name VARCHAR(191) NOT NULL,
	gender VARCHAR(191),
	PRIMARY KEY(id)
);

CREATE TABLE User (
	id INT NOT NULL,
	profileId INT NOT NULL,
	CONSTRAINT FK_USER_PROFILE_0 FOREIGN KEY(profileId) REFERENCES Profile(id),
	PRIMARY KEY(id)
);