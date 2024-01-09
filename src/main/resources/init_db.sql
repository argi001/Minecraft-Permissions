create table if not exists {PREFIX}user_group
(
    id     int auto_increment primary key,
    name   varchar(255) not null,
    prefix varchar(255) null
);

create table if not exists {PREFIX}permission
(
    id int auto_increment primary key,
    permission_string varchar(255) not null
);

create table if not exists {PREFIX}group2permission
(
    group_id int not null,
    permission_id int not null,
    primary key (group_id, permission_id),
    constraint group2permission_group_id_fk
    foreign key (group_id) references {PREFIX}user_group (id),
    constraint group2permission_permission_id_fk
    foreign key (permission_id) references {PREFIX}permission (id)
    );

create table if not exists {PREFIX}player
(
    uuid varchar(255) not null primary key,
    display_name varchar(255) not null,
    create_date  timestamp default CURRENT_TIMESTAMP null
    );

create table if not exists {PREFIX}player2group
(
    id              int auto_increment
    primary key,
    player_uuid     varchar(255)                        not null,
    group_id        int                                 not null,
    expire_datetime datetime                            null,
    create_date     timestamp default CURRENT_TIMESTAMP null,
    last_group_id   int                                 null,
    constraint player2group_player_uuid_fk
    foreign key (player_uuid) references {PREFIX}player (uuid),
    constraint player2group_user_group_id_fk
    foreign key (group_id) references {PREFIX}user_group (id)
    );