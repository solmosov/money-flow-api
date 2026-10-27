
    create table categories (
        created_at timestamp(6) with time zone not null,
        deleted_at timestamp(6) with time zone,
        updated_at timestamp(6) with time zone,
        used_at timestamp(6) with time zone,
        id uuid not null,
        user_id uuid not null,
        icon_name varchar(20) not null,
        name varchar(255) not null,
        type varchar(255) not null check ((type in ('INCOME','EXPENSE'))),
        primary key (id)
    );

    create table currencies (
        code varchar(3) not null,
        decimals integer not null,
        is_active boolean not null,
        symbol varchar(8) not null,
        name varchar(64) not null,
        primary key (code)
    );

    create table export_jobs (
        created_at timestamp(6) with time zone not null,
        date_from timestamp(6) with time zone not null,
        date_to timestamp(6) with time zone not null,
        expires_at timestamp(6) with time zone not null,
        file_size bigint,
        finished_at timestamp(6) with time zone,
        format varchar(8) not null check ((format in ('CSV','PDF'))),
        updated_at timestamp(6) with time zone,
        id uuid not null,
        status varchar(16) not null check ((status in ('PENDING','RUNNING','READY','FAILED'))),
        user_id uuid not null,
        zone_id varchar(64) not null,
        file_name varchar(100),
        download_name varchar(128),
        error_message varchar(500),
        primary key (id)
    );

    create table goals (
        base_amount numeric(14,2) not null,
        current_code varchar(3) not null,
        rate_snapshot numeric(8,2) not null,
        starting_amount numeric(14,2) not null,
        target_amount numeric(14,2) not null,
        completed_at timestamp(6) with time zone,
        created_at timestamp(6) with time zone not null,
        deadline timestamp(6) with time zone not null,
        updated_at timestamp(6) with time zone,
        status varchar(14) not null check ((status in ('ACTIVE','COMPLETED'))),
        id uuid not null,
        user_id uuid not null,
        wallet_id uuid not null,
        name varchar(80) not null,
        primary key (id)
    );

    create table roles (
        created_at timestamp(6) with time zone not null,
        updated_at timestamp(6) with time zone,
        id uuid not null,
        name varchar(255) not null unique check ((name in ('ADMIN','USER'))),
        primary key (id)
    );

    create table transactions (
        amount numeric(14,2) not null,
        currency_code varchar(3) not null,
        rate_amount numeric(20,10) not null,
        to_amount numeric(14,2),
        created_at timestamp(6) with time zone not null,
        date timestamp(6) with time zone not null,
        updated_at timestamp(6) with time zone,
        category_id uuid,
        id uuid not null,
        to_wallet_id uuid,
        user_id uuid not null,
        wallet_id uuid not null,
        description varchar(255),
        type varchar(255) not null check ((type in ('INCOME','EXPENSE','TRANSFER'))),
        primary key (id)
    );

    create table user_role (
        role_id uuid not null,
        user_id uuid not null,
        unique (user_id, role_id)
    );

    create table users (
        base_currency_code varchar(3),
        created_at timestamp(6) with time zone not null,
        telegram_user_id bigint unique,
        updated_at timestamp(6) with time zone,
        id uuid not null,
        email varchar(80) unique,
        password varchar(255),
        primary key (id)
    );

    create table wallets (
        balance numeric(14,2) not null,
        currency_code varchar(3) not null,
        rate_amount numeric(20,10) not null,
        archived_at timestamp(6) with time zone,
        created_at timestamp(6) with time zone not null,
        updated_at timestamp(6) with time zone,
        type varchar(10) not null check ((type in ('CASH','CARD','SAVINGS','GOAL','DEBT'))),
        id uuid not null,
        user_id uuid not null,
        name varchar(50) not null,
        primary key (id)
    );

    create index idx_categories_user_id
       on categories (user_id);

    create index idx_transactions_user_date
       on transactions (user_id, date);

    create index idx_transactions_user_wallet_date
       on transactions (user_id, wallet_id, date desc);

    create index idx_transactions_user_type_date
       on transactions (user_id, type, date desc);

    create index idx_users_email
       on users (email);

    create index idx_users_telegram_user
       on users (telegram_user_id);

    create index idx_wallets_user_id_archived_at
       on wallets (user_id, archived_at);

    alter table if exists categories
       add constraint FKghuylkwuedgl2qahxjt8g41kb
       foreign key (user_id)
       references users
       on delete cascade;

    alter table if exists goals
       add constraint FKj2714ionvgaavg5ljx3hty5vw
       foreign key (current_code)
       references currencies;

    alter table if exists goals
       add constraint FKb1mp6ulyqkpcw6bc1a2mr7v1g
       foreign key (user_id)
       references users
       on delete cascade;

    alter table if exists goals
       add constraint FKufp5uo7or0lqny7j26q0edvj
       foreign key (wallet_id)
       references wallets
       on delete cascade;

    alter table if exists transactions
       add constraint FKsqqi7sneo04kast0o138h19mv
       foreign key (category_id)
       references categories
       on delete cascade;

    alter table if exists transactions
       add constraint FKhyjo8jgl852u59352ujfwohuo
       foreign key (currency_code)
       references currencies;

    alter table if exists transactions
       add constraint FKsgetvdqt8t51mmsxqv2ypo0l0
       foreign key (to_wallet_id)
       references wallets
       on delete cascade;

    alter table if exists transactions
       add constraint FKqwv7rmvc8va8rep7piikrojds
       foreign key (user_id)
       references users
       on delete cascade;

    alter table if exists transactions
       add constraint FK23bop5lktue0o5q7kr19ti8h
       foreign key (wallet_id)
       references wallets
       on delete cascade;

    alter table if exists user_role
       add constraint FKt7e7djp752sqn6w22i6ocqy6q
       foreign key (role_id)
       references roles;

    alter table if exists user_role
       add constraint FKj345gk1bovqvfame88rcx7yyx
       foreign key (user_id)
       references users;

    alter table if exists users
       add constraint FKf6fa5j9qafvr5nh1v9u6v6eqv
       foreign key (base_currency_code)
       references currencies;

    alter table if exists wallets
       add constraint FK264tocyk9ilq23qm6cvugxgye
       foreign key (currency_code)
       references currencies;

    alter table if exists wallets
       add constraint FKc1foyisidw7wqqrkamafuwn4e
       foreign key (user_id)
       references users
       on delete cascade;
