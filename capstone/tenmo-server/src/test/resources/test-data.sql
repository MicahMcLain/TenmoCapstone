BEGIN TRANSACTION;

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS tenmo_user;
DROP SEQUENCE IF EXISTS seq_user_id;

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) UNIQUE NOT NULL,
	password_hash varchar(200) NOT NULL,
	balance int DEFAULT 1000,
	role varchar(20),
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);
CREATE TABLE transactions (
	transactions_id SERIAL,
	sending_user_id int NOT NULL,
	receiving_user_id int NOT NULL,
	amount int NOT NULL,
	date_time date NOT NULL,
	status int null,
	CONSTRAINT PK_transactions_id PRIMARY KEY (transactions_id),
	CONSTRAINT FK_sending_user_id FOREIGN KEY (sending_user_id) REFERENCES tenmo_user(user_id),
	CONSTRAINT FK_receiving_user_id FOREIGN KEY (receiving_user_id) REFERENCES tenmo_user(user_id)

INSERT INTO tenmo_user (username,password_hash,role) VALUES ('user1','user1','ROLE_USER'); -- 1001
INSERT INTO tenmo_user (username,password_hash,role) VALUES ('user2','user2','ROLE_USER'); -- 1002
INSERT INTO tenmo_user (username,password_hash,role) VALUES ('user3','user3','ROLE_USER');

COMMIT TRANSACTION;
