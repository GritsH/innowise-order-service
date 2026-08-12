create table orderservice.order
(
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    status      varchar(50)    not null,
    total_price numeric(10, 2) not null,
    deleted     boolean        not null default false,
    created_at timestamptz      default now(),
    updated_at timestamptz      default now()
);

create index idx_user_id on orderservice.order (user_id);
create index idx_user_id_status on orderservice.order (user_id, status);
