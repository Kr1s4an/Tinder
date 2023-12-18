create table user(
   USER_ID int not null,
   FRIEND_ID int not null
   PRIMARY KEY (user_id, friend_id),
   FOREIGN KEY (user_id) REFERENCES user(USER_ID),
   FOREIGN KEY (friend_id) REFERENCES user(USER_ID)
);