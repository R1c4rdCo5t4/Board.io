begin;

create table if not exists "user"(
    id serial primary key,
    name varchar(30) not null,
    email varchar(30) unique not null check (email ~ '^[A-Za-z0-9+_.-]+@(.+)$'),
    token char(36) unique not null,
    password char(64) not null,
    createdDate timestamp not null default current_timestamp
);

create table if not exists board(
    id serial primary key,
    name varchar(30) unique not null,
    description varchar(50) not null,
    createdDate timestamp not null default current_timestamp
);

create table if not exists userBoard(
    userId int not null references "user" on delete cascade on update cascade,
    boardId int not null references board on delete cascade on update cascade,
    constraint userboard_pk primary key (userId, boardId)
);

create table if not exists list(
    id serial primary key,
    name varchar(30) not null,
    boardId int not null references board on delete cascade on update cascade,
    index int not null,
    createdDate timestamp not null default current_timestamp,
    archived boolean not null default false
);

create table if not exists card(
    id serial primary key,
    name varchar(30) not null,
    description varchar(50) not null,
    listId int not null references list on delete cascade on update cascade,
    dueDate timestamp check (dueDate > createdDate),
    index int not null,
    createdDate timestamp not null default current_timestamp,
    archived boolean not null default false
);

commit;