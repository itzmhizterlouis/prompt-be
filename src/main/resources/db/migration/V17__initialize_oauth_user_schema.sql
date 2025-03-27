create table auth_users(
    oauth_user_id uuid primary key,
    email varchar not null unique
);
