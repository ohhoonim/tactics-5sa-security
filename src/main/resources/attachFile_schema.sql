-- =============== attachfile 
CREATE TABLE if not exists SYSTEM_ATTACH_FILE (
    attach_file_id uuid not null,
    is_linked boolean default false,
    created_at timestamptz not null,
    created_by uuid,
    modified_at timestamptz,
    modified_by uuid,

    constraint pk_attach_file primary key (attach_file_id)
);

CREATE TABLE if not exists SYSTEM_FILE_ITEM (
    file_item_id uuid not null,
    attach_file_id uuid not null,
    origin_name text not null,
    uploaded_path text not null,
    capacity bigint not null,
    extension text,
    is_removed boolean default false,

    constraint pk_file_item primary key (file_item_id),
    constraint fk_system_attach_file foreign key (attach_file_id) references system_attach_file(attach_file_id) 
);

CREATE INDEX if not exists idx_file_item_attach_file_id ON SYSTEM_FILE_ITEM(file_item_id, attach_file_id);

