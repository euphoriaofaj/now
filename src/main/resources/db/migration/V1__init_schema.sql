-- ===== CREATE TABLES =====
create table calls (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    call_status enum ('CONNECT_NOT_SET','ERROR','FCM_AND_APN_TOKEN_NULL','MISSED','PUSH_NOT_SENT','SUCCESSFUL','TRANSLATOR_NOT_AVAILABLE','TRANSLATOR_NOT_ONLINE') not null,
    channel_name varchar(50),
    commission decimal(10,2),
    duration integer not null,
    is_end_call bit,
    status bit not null,
    sum_decimal decimal(10,2),
    translator_has_joined bit not null,
    user_has_rated bit not null,
    caller_id bigint not null,
    recipient_id bigint not null,
    theme_id bigint,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table categories (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    is_active bit not null,
    name varchar(255),
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table debtors (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    account_holder varchar(200) not null,
    is_paid bit,
    name_of_bank varchar(200) not null,
    user_id bigint,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table deposits (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    account_holder varchar(200) not null,
    coin_decimal decimal(10,2) not null,
    name_of_bank varchar(200) not null,
    status enum ('FAILED','INCOMPLETE','PENDING','SUCCESSFUL') not null,
    won_decimal decimal(10,2) not null,
    user_id bigint not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table files (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    original_title varchar(255),
    path varchar(512) not null,
    type varchar(100),
    user_id bigint,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table languages (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    name varchar(200) not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table notifications (
    id bigint not null auto_increment,
    date_time datetime(6),
    error_time datetime(6),
    is_read bit not null,
    text varchar(1000),
    title varchar(200),
    user_id bigint,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table password_resets (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    phone varchar(100),
    reset_code integer,
    token varchar(255),
    user_id bigint not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table ratings (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    comment varchar(1000),
    score integer not null,
    translator_profile_id bigint not null,
    user_id bigint not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table refresh_tokens (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    expiration_time datetime(6) not null,
    token varchar(600) not null,
    user_id bigint not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table roles (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    role_name enum ('ROLE_ADMIN','ROLE_TRANSLATOR','ROLE_USER') not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table themes (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    description varchar(500),
    is_active bit,
    is_popular bit,
    korean_title varchar(200),
    name varchar(200) not null,
    night_price decimal(10,2),
    price decimal(10,2),
    category_id bigint,
    icon_file_id bigint,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table translator_languages (
    translator_profile_id bigint not null,
    language_id bigint not null,
    primary key (translator_profile_id, language_id)
) engine=InnoDB default charset=utf8mb4;

create table translator_profiles (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    date_of_birth date,
    email varchar(320),
    is_available bit,
    is_online bit,
    level_of_korean varchar(200),
    user_id bigint not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table translator_themes (
    translator_profile_id bigint not null,
    theme_id bigint not null,
    primary key (translator_profile_id, theme_id)
) engine=InnoDB default charset=utf8mb4;

create table user_profiles (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    is_free_call_made bit not null,
    user_id bigint not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table user_roles (
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id)
) engine=InnoDB default charset=utf8mb4;

create table users (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    balance decimal(21,2),
    first_name varchar(200),
    is_active bit not null,
    last_name varchar(200),
    on_boarding_status tinyint,
    password varchar(255) not null,
    phone_number varchar(20) not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table withdrawals (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6),
    account_holder varchar(200) not null,
    account_number varchar(200) not null,
    name_of_bank varchar(200) not null,
    status enum ('FAILED','INCOMPLETE','PENDING','SUCCESSFUL') not null,
    sum_decimal decimal(10,2) not null,
    user_id bigint not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

-- ===== UNIQUE CONSTRAINTS =====
alter table categories add constraint uk_categories_name unique (name);
alter table files add constraint UKi2xe46u1dge0ksxkhljlg2ouu unique (path);
alter table languages add constraint UKf6axmaokhmrbmm746866v0uyu unique (name);
alter table password_resets add constraint UKj3in8q8o0ve0i34pyug2kgibh unique (token);
alter table ratings add constraint uk_ratings_user_translator unique (user_id, translator_profile_id);
alter table refresh_tokens add constraint UKghpmfn23vmxfu3spu3lfg4r2d unique (token);
alter table roles add constraint uk_roles_role_name unique (role_name);
alter table themes add constraint UK3estny12ybh85k7y8j6gyyrep unique (name);
alter table themes add constraint UKe57ky2jkw9f0ct4in26osep0w unique (icon_file_id);
alter table translator_profiles add constraint UK667qggokvg3kfnkxi8og61549 unique (email);
alter table translator_profiles add constraint UKgg7pk63420j2ekrhv96y7my6o unique (user_id);
alter table user_profiles add constraint UKe5h89rk3ijvdmaiig4srogdc6 unique (user_id);
alter table users add constraint UK9q63snka3mdh91as4io72espi unique (phone_number);

-- ===== FOREIGN KEYS =====
alter table calls add constraint FKd982s1pgb8m50bf9wvu08hpfk foreign key (caller_id) references users (id);
alter table calls add constraint FKb7u43fqqex8h28durb6nj8a1r foreign key (recipient_id) references users (id);
alter table calls add constraint FKa6gaqkmia1o34ckft4436moji foreign key (theme_id) references themes (id);

alter table debtors add constraint FKpgsha4ofn1e7enq3miwr3u8l8 foreign key (user_id) references users (id);

alter table deposits add constraint FK6rrn8357gkkm2l4djgd4d3hke foreign key (user_id) references users (id);

alter table files add constraint FKdgr5hx49828s5vhjo1s8q3wdp foreign key (user_id) references users (id);

alter table notifications add constraint FK9y21adhxn0ayjhfocscqox7bh foreign key (user_id) references users (id);

alter table password_resets add constraint FKfy4ulhbvy3yguwnqqvts2iqqx foreign key (user_id) references users (id);

alter table ratings add constraint FK5nemlmnqxj6niyqxvih37p0h4 foreign key (translator_profile_id) references translator_profiles (id);
alter table ratings add constraint FKb3354ee2xxvdrbyq9f42jdayd foreign key (user_id) references users (id);

alter table refresh_tokens add constraint FK1lih5y2npsf8u5o3vhdb9y0os foreign key (user_id) references users (id);

alter table themes add constraint FKtrkcvsb7vv36uoybe1kvc7u4t foreign key (category_id) references categories (id);
alter table themes add constraint FKivit9xpr41bt2skigfu4ymf4d foreign key (icon_file_id) references files (id);

alter table translator_languages add constraint FK97bklkrb6av82m7bh3j4gcn9f foreign key (language_id) references languages (id);
alter table translator_languages add constraint FKjvb5h7o2semp91pwf8je70rmq foreign key (translator_profile_id) references translator_profiles (id);

alter table translator_profiles add constraint FKhj2jn9skwgha04dcnj6o4ago0 foreign key (user_id) references users (id);

alter table translator_themes add constraint FKtbbbp8ke40n4spavgso7b3f4v foreign key (theme_id) references themes (id);
alter table translator_themes add constraint FKdso6wiubw24mp531jsukkciyq foreign key (translator_profile_id) references translator_profiles (id);

alter table user_profiles add constraint FKjcad5nfve11khsnpwj1mv8frj foreign key (user_id) references users (id);

alter table user_roles add constraint FKh8ciramu9cc9q3qcqiv4ue8a6 foreign key (role_id) references roles (id);
alter table user_roles add constraint FKhfh9dx7w3ubf1co1vdev94g3f foreign key (user_id) references users (id);

alter table withdrawals add constraint FKesk6migh8b3x43q3740dh5fja foreign key (user_id) references users (id);
