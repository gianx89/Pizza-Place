create table order_statuses (order_status_id uuid not null, status varchar(255), primary key (order_status_id));
create table orders (created_date timestamp(6), last_modified_date timestamp(6), order_id uuid not null, order_status_order_status_id uuid, pizzas varchar(255) array, primary key (order_id));
alter table if exists orders add constraint FKonxtg4qet51il6ioosgj48e2u foreign key (order_status_order_status_id) references order_statuses;

INSERT INTO order_statuses (order_status_id,status) VALUES
	 ('addf422c-4b37-4631-b0d0-3cfcbb68fe41','RECEVIED'),
	 ('df350171-e428-4d2c-a6c4-31123ef40ead','CANCELLED'),
	 ('6f0747ae-324e-4178-970d-9cda7cc03968','PROCESSING'),
	 ('cb90a068-10b8-4753-b55a-cdeadc2ef573','COMPLETED');