-- liquibase formatted sql

-- changeset s.yukov:1
create table if not exists notification_task(
    id serial primary key,
    chat_id serial,
    text_notification text not null,
    sending_date_time timestamp
);





