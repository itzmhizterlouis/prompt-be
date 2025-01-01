create table if not exists payments (

    payment_id uuid primary key,
    status varchar(50) not null,
    method varchar(50) not null,
    amount int not null,

    order_id uuid not null unique,

    constraint fk_payments_order_id foreign key (order_id)
                                    references orders(order_id)
);

alter table orders
    drop column completed;
