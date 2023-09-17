drop table if exists users, requests, items, bookings, comments;

create table if not exists users (
  id bigint generated by default as identity not null,
  name varchar(255) not null,
  email varchar(512) not null,
  constraint pk_user primary key (id),
  constraint UQ_USER_EMAIL unique (email)
);

create table if not exists requests (
  id bigint generated by default as identity not null,
  description varchar,
  --requestor_id bigint references users (id) on delete cascade,
  created timestamp without time zone,
  constraint pk_request primary key (id)
);

create table if not exists items (
  id bigint generated by default as identity not null,
  name varchar(255) not null,
  description varchar not null,
  is_available boolean not null,
  owner_id bigint references users (id) on delete cascade not null,
  --request_id bigint references requests (id) on delete cascade,
  constraint pk_item primary key (id)
);

 create table if not exists comments (
   id bigint generated by default as identity not null,
   text varchar not null,
   item_id bigint references items (id) on delete cascade not null,
   author_id bigint references users (id) on delete cascade not null,
   created timestamp without time zone,
   constraint pk_comment primary key (id)
 );

create table if not exists bookings (
  id bigint generated by default as identity not null,
  start_date timestamp without time zone not null,
  end_date timestamp without time zone not null,
  item_id bigint references items (id) on delete cascade not null,
  booker_id bigint references users (id) on delete cascade not null,
  status varchar not null,
  constraint pk_booking primary key (id)
);

