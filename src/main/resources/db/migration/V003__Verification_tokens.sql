create table verification_tokens(
    ID int not null AUTO_INCREMENT PRIMARY KEY,
    USER_ID int,
    TOKEN VARCHAR(36) DEFAULT (uuid()),
    EXPIRATION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (USER_ID) REFERENCES user(ID)
);

INSERT into verification_tokens (USER_ID)
values(1);