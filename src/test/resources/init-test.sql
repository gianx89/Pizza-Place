create table order_statuses (order_status_id uuid not null, status varchar(255), primary key (order_status_id));
create table orders (created_date timestamp(6), last_modified_date timestamp(6), order_id uuid not null, order_status_order_status_id uuid, pizzas varchar(255) array, primary key (order_id));
alter table if exists orders add constraint FKonxtg4qet51il6ioosgj48e2u foreign key (order_status_order_status_id) references order_statuses;

INSERT INTO order_statuses (order_status_id,status) VALUES
	 ('addf422c-4b37-4631-b0d0-3cfcbb68fe41','RECEVIED'),
	 ('df350171-e428-4d2c-a6c4-31123ef40ead','CANCELLED'),
	 ('6f0747ae-324e-4178-970d-9cda7cc03968','PROCESSING'),
	 ('cb90a068-10b8-4753-b55a-cdeadc2ef573','COMPLETED');

INSERT INTO orders (created_date,last_modified_date,order_id,order_status_order_status_id,pizzas) VALUES
	 ('2024-02-15 01:39:42.783402','2024-02-16 04:45:27.447545','c2292f78-ca47-432b-b5cf-df0b0c739592','6f0747ae-324e-4178-970d-9cda7cc03968','{"Quattro stagioni",Affumicata,Deliziosa}'),
	 ('2024-02-15 01:42:11.955226','2024-02-16 04:40:06.017546','22a80655-1aac-4d3b-9b59-3902616b21a5','addf422c-4b37-4631-b0d0-3cfcbb68fe41','{"Quattro stagioni",Affumicata,Deliziosa}'),
	 ('2024-02-15 01:43:13.979753','2024-02-15 01:43:13.979753','0b0fa7e5-7b7b-40a0-80ce-f7da87e1463a','addf422c-4b37-4631-b0d0-3cfcbb68fe41','{"Quattro stagioni",Affumicata,Deliziosa}'),
	 ('2024-02-15 01:44:18.804662','2024-02-15 01:44:18.804662','280cf23b-701c-4368-a28d-2392971ff324','addf422c-4b37-4631-b0d0-3cfcbb68fe41','{"Quattro stagioni",Affumicata,Deliziosa}');
