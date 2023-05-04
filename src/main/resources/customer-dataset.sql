create table customer
(
    id      serial primary key,
    email   varchar(50)  not null,
    name    varchar(100) not null,
    country varchar(20)  not null,
    age     int          not null
);
insert into customer (name, email, country, age)
values ('John Doe', 'john@doe.com', 'US', 25);
insert into customer (name, email, country, age)
values ('Jane Doe', 'jane@doe.com', 'US', 27);
insert into customer (name, email, country, age)
values ('Seamus Murphy', 's.murphy@abc.co', 'UK', 35);
insert into customer (name, email, country, age)
values ('Toby Flenderson', 'toby.flenderson@dundermifflin.com', 'CR', 45);
insert into customer (name, email, country, age)
values ('James Doe', 'james@email.com', 'US', 16);
commit;
