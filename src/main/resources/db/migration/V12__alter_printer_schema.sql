alter table printers
    alter column account_number type varchar using account_number::text;
