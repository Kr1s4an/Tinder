create table verification_tokens(
    ID int not null AUTO_INCREMENT PRIMARY KEY,
    USER_ID int not null,
    TOKEN varchar(50) not null,
    EXPIRATION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP
);