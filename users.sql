drop table if exists users;
drop table if exists user_role;

create table user_role(
	id integer primary key,
	role_name varchar(45) not null unique
);

create table users (
	id integer primary key autoincrement,
        user_name varchar(18) not null unique,
	password  varchar(45),
	nick_name varchar(45) not null,
	user_role_id integer not null references user_role(id)	
);

insert into user_role (id,role_name) values (1,'admin');
insert into user_role (id,role_name) values (2,'teacher');
insert into user_role (id,role_name) values (3,'guest');

insert into users (user_name,nick_name,user_role_id) values ('admin','Администратор',1);