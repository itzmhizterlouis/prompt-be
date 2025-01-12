alter table payments
    add column fee int not null default 0;

alter table order_payments
    add column fee int not null default 0;
