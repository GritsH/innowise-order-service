create table orderservice.item
(
    id uuid primary key default gen_random_uuid(),
    name  varchar(255)   not null,
    price numeric(10, 2) not null,
    created_at timestamptz      default now(),
    updated_at timestamptz      default now()
);

create index idx_name on orderservice.item (name);
create index idx_name_price on orderservice.item (name, price);