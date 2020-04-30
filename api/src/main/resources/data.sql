
insert into user
values(101, 'admin@gmail.com', 'admin', 'admin',  'admin', '$2a$10$5nswfu3hiT57trDJINBy8OMOtJXS65DmiWDbo9mQyqgNUdSrnvkMi', '021 012 012');


insert into user
values(102, 'user@gmail.com', 'Jack', 'Bauer',  'user', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K', '9999');


insert into user
values(103, 'test', 'test', 'test',  'test', 'test', '9999');

insert into role
values ('ROLE_ADMIN');

insert into role
values ('ROLE_USER') ;

insert into USER_ROLE
values (101, 'ROLE_ADMIN');

insert into USER_ROLE
values (102, 'ROLE_USER');

insert into USER_ROLE
values (103, 'ROLE_USER');
