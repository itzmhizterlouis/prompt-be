create table if not exists reviews (

    review_id uuid primary key,
    rating int not null,
    comment varchar(255) not null,

    owner_id uuid not null,
    printer_id uuid not null,


    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),

    unique (owner_id, printer_id),

    constraint fk_reviews_owner_id foreign key (owner_id)
                                   references users(user_id),
    constraint fk_reviews_printer_id foreign key (printer_id)
                                   references printers(printer_id)
);
