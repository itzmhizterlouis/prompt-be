alter table payments
    add column payment_type varchar(255) default 'PAY_IN' not null;
