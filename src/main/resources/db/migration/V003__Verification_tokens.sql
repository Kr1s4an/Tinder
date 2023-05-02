create table verification_tokens(
    ID int not null AUTO_INCREMENT PRIMARY KEY,
    USER_ID int ,
    TOKEN varchar(50) not null,
    EXPIRATION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (USER_ID) REFERENCES user(ID)
);