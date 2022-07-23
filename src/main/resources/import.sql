-- customers
insert into customers (email, first_name,last_name) values ( 'berlisy2j@hotmail.com','berlis','rodriguez')
insert into customers (email, first_name,last_name) values ( 'amalfishernandez@hotmail.com','amalfis','hernandez')

-- products
insert into products (description,price,stock) values ('iphone 6', 1000,10);
insert into products (description,price,stock) values ('iphone 7', 1200,10);
insert into products (description,price,stock) values ('iphone 8', 1300,10);
insert into products (description,price,stock) values ('iphone 9', 1400,10);
insert into products (description,price,stock) values ('iphone x', 1500,10);
insert into products (description,price,stock) values ('iphone 11', 1600,10);

-- addresses
insert into addresses (description, customer_id) values ('MIAMI, FL 33191-1501 8260 NW 14TH ST EPS # NG-1748', 1)
insert into addresses (description, customer_id) values ('ORLANDO, OL 51492-1501 6270 NW 14TH ST EPS # NG-1748', 1)
insert into addresses (description, customer_id) values ('OHIO, OH 67492-1501 6270 NW 14TH ST EPS # NG-1748', 2)
insert into addresses (description, customer_id) values ('BOSTON, BO 21492-1501 6270 NW 14TH ST EPS # NG-1748', 2)

-- payment_methods
insert into payment_methods (balance, number_reference, payment_type, customer_id) values (100000, '4002000000000001', 'debit card', 1)
insert into payment_methods (balance, number_reference, payment_type, customer_id) values (100000, '6023000000000002', 'credit card', 1)
insert into payment_methods (balance, number_reference, payment_type, customer_id) values (100000, 'dfew000000000003', 'bank account', 1)
insert into payment_methods (balance, number_reference, payment_type, customer_id) values (100000, '4444000000000004', 'debit card', 2)
insert into payment_methods (balance, number_reference, payment_type, customer_id) values (100000, '8883000000000005', 'credit card', 2)
insert into payment_methods (balance, number_reference, payment_type, customer_id) values (100000, 'wers000000000006', 'bank account', 2)