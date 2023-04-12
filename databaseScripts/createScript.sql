create table repairs
(
    repair_id  bigint auto_increment
        primary key,
    name       varchar(32)          not null,
    price      double(10, 2)        not null,
    is_deleted tinyint(1) default 0 not null
);

create table roles
(
    role_id bigint auto_increment
        primary key,
    name    varchar(32) not null,
    constraint roles_pk
        unique (name)
);

create table users
(
    user_id      bigint auto_increment
        primary key,
    username     varchar(20)  not null,
    email        varchar(255) not null,
    phone_number char(10)     not null,
    password     varchar(256) not null,
    first_name   varchar(32)  null,
    last_name    varchar(32)  null,
    constraint users_pk
        unique (username),
    constraint users_pk2
        unique (email),
    constraint users_pk3
        unique (phone_number)
);

create table users_roles
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint users_roles_roles_role_id_fk
        foreign key (role_id) references roles (role_id),
    constraint users_roles_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table vehicles
(
    vehicle_id      bigint auto_increment
        primary key,
    license_plate   varchar(10) not null,
    vin             char(17)    not null,
    production_year int         not null,
    model           varchar(32) not null,
    brand           varchar(32) not null,
    user_id         bigint      not null,
    constraint vehicles_pk2
        unique (license_plate),
    constraint vehicles_pk3
        unique (vin),
    constraint vehicles_users_user_id_fk
        foreign key (user_id) references users (user_id),
    constraint check_production_year
        check (`production_year` >= 1886)
);

create table visits
(
    visit_id   bigint auto_increment
        primary key,
    date       datetime not null,
    vehicle_id bigint   not null,
    user_id    bigint   not null,
    constraint visits_users_user_id_fk
        foreign key (user_id) references users (user_id),
    constraint visits_vehicles_vehicle_id_fk
        foreign key (vehicle_id) references vehicles (vehicle_id)
);

create table visits_repairs
(
    visit_id  bigint not null,
    repair_id bigint not null,
    primary key (visit_id, repair_id),
    constraint visits_repairs_repairs_repair_id_fk
        foreign key (repair_id) references repairs (repair_id),
    constraint visits_repairs_visits_visit_id_fk
        foreign key (visit_id) references visits (visit_id)
);

