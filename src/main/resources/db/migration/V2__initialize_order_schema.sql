create table if not exists orders (

    order_id uuid primary key,
    description varchar(255) default null,
    payment_type varchar(50) not null,
    status varchar(50) not null,
    charge int not null,
    paid boolean not null default false,

    customer_id uuid not null,
    printer_id uuid not null,

    created_at timestamp not null default now(),
    time_expected timestamp not null default now(),
    updated_at timestamp not null default now(),

    constraint fk_orders_customer_id foreign key (customer_id)
                                  references users(user_id),
    constraint fk_orders_printer_id foreign key (printer_id)
                                  references printers(printer_id)
);

create table if not exists order_documents(

    order_document_id uuid primary key,
    name varchar(50) not null,
    uri varchar not null,
    copies int not null default 1,
    pages int not null default 1,
    coloured boolean not null default false,

    order_id uuid not null,

    constraint fk_order_documents_order_id foreign key (order_id)
                                          references orders(order_id)
);
