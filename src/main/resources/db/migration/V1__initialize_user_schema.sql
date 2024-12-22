create table if not exists users (

    user_id uuid primary key not null,

    first_name varchar(50) not null,
    last_name varchar(50) not null,
    email varchar(50) not null unique,
    is_printer boolean not null default false,
    account_non_expired boolean not null default true,
    account_non_locked boolean not null default true,
    enabled boolean not null default true,
    credentials_non_expired boolean not null default true,
    email_verified boolean not null default false,

    password varchar(255) not null,

    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create table if not exists user_otps (

    id serial primary key not null,

    user_id uuid not null,
    otp int not null,
    created_at timestamp not null default now(),

    unique (user_id, otp),

    constraint fk_user_otp_user_id foreign key (user_id)
                                     references users(user_id)
);

create table if not exists printers (

    printer_id uuid primary key not null,
    name varchar(50) not null unique,
    location varchar(50) not null,
    description varchar(255),
    coloured_rate int not null,
    uncoloured_rate int not null,
    bank_name varchar(50) not null,
    account_name varchar(50) not null,
    account_number int not null,

    weekday_closing timestamp not null,
    weekday_opening timestamp not null,
    weekend_closing timestamp not null,
    weekend_opening timestamp not null,

    user_id uuid not null unique,

    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create table if not exists printer_wallet(

     printer_wallet_id uuid primary key,
     balance int not null default 0,
     created_at timestamp not null default now(),
     updated_at timestamp not null default now(),

     printer_id uuid not null unique,

     constraint fk_printer_wallet_printer_id foreign key (printer_id)
         references printers(printer_id)
);
