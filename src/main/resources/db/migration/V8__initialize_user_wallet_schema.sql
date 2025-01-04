drop table printer_wallet;

create table if not exists user_wallets (

    user_id uuid primary key,
    balance int not null default 0,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),

    constraint fk_user_wallets_user_id foreign key (user_id)
        references users(user_id)
);
