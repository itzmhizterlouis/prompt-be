drop table payments;

create table if not exists payments (

    payment_id uuid primary key,
    status varchar(50) not null,
    method varchar(50) not null,
    amount int not null,

    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),

    user_id uuid not null unique,

    constraint fk_payments_user_id foreign key (user_id)
        references users(user_id)
);

create table if not exists order_payments (

    payment_id uuid primary key,
    status varchar(50) not null,
    method varchar(50) not null,
    amount int not null,

    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),

    order_id uuid not null,

    constraint fk_payments_order_id foreign key (order_id)
    references orders(order_id)
);