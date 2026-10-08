create table roles
(
    id         uuid                        not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone,
    name       varchar(255)                not null,
    primary key (id),
    constraint uk_roles_name unique (name),
    constraint chk_roles_name check (name in ('ADMIN', 'USER'))
);

create table users
(
    id               uuid                        not null,
    created_at       timestamp(6) with time zone not null,
    updated_at       timestamp(6) with time zone,
    email            varchar(80),
    password         varchar(255),
    telegram_user_id bigint,
    balance          numeric(14, 2)              not null,
    primary key (id),
    constraint uk_users_email unique (email),
    constraint uk_users_telegram_user_id unique (telegram_user_id)
);

create table user_role
(
    user_id uuid not null,
    role_id uuid not null,
    constraint uk_user_role_user_id_role_id unique (user_id, role_id),
    constraint fk_user_role_user foreign key (user_id) references users (id),
    constraint fk_user_role_role foreign key (role_id) references roles (id)
);

create table categories
(
    id         uuid                        not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone,
    deleted_at timestamp(6) with time zone,
    user_id    uuid                        not null,
    name       varchar(255)                not null,
    type       varchar(255)                not null,
    icon_name  varchar(20)                 not null,
    primary key (id),
    constraint chk_categories_type check (type in ('INCOME', 'EXPENSE')),
    constraint fk_categories_user foreign key (user_id) references users (id)
);

create table transactions
(
    id          uuid                        not null,
    created_at  timestamp(6) with time zone not null,
    updated_at  timestamp(6) with time zone,
    user_id     uuid                        not null,
    category_id uuid                        not null,
    type        varchar(255)                not null,
    amount      numeric(14, 2)              not null,
    currency    varchar(255),
    date        timestamp(6) with time zone not null,
    description varchar(255),
    primary key (id),
    constraint chk_transactions_type check (type in ('INCOME', 'EXPENSE', 'TO_LEND', 'TO_BORROW')),
    constraint chk_transactions_currency check (currency in ('UZS', 'USD', 'RUB')),
    constraint fk_transactions_user foreign key (user_id) references users (id),
    constraint fk_transactions_category foreign key (category_id) references categories (id)
);