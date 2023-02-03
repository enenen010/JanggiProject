create database 12kchess;

USE 12Kchess;

create table Cuser(
	id varchar(40) not null PRIMARY KEY,
	pw varchar(32) not null 
);

create table Notation(
	Nkey VARCHAR(100) PRIMARY KEY,
	id varchar(40) NOT NULL,
	notation VARCHAR(5000) NOT NULL,
	title VARCHAR(500) DEFAULT 'no title',
	foreign key (id) references Cuser(id)
);

