create table orderservice.order_item
(
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null,
    item_id uuid not null,
    quantity integer not null,
    created_at timestamptz      default now(),
    updated_at timestamptz      default now(),
    constraint fk_order_items_order foreign key (order_id) references orderservice.order (id),
    constraint fk_order_items_item foreign key (item_id) references orderservice.item (id)
);

create index idx_order_id on orderservice.order_item (order_id);
create index idx_order_id_item_id on orderservice.order_item (order_id, item_id);