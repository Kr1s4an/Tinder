create table friend (
   USER_ID int not null,
   FRIEND_ID int not null,
   PRIMARY KEY (USER_ID, FRIEND_ID),
   FOREIGN KEY (USER_ID) REFERENCES user(ID),
   FOREIGN KEY (FRIEND_ID) REFERENCES user(ID)
);