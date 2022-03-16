INSERT INTO paymybuddytest.users (first_name,last_name,email,password,enabled,github_user,google_user,local_user) VALUES
	 ('admin',NULL,'admin@email.com',0x243279243130244B432F76762F2E4B67536C51326D676B6538674A662E36716B4F596641546F796B3535715057366331776E63773247427151657569,1,NULL,NULL,1),
	 ('user',NULL,'user@email.com',0x24327924313024455632454330667341564E737256712E63694E364E4F56434B506D4B5A3863536431496B38427A38635A666A394E754E6A79666F32,1,NULL,NULL,1),
	 ('james','smith','james@mail.com',0x243279243130245A465254595A6F493254723361504B334F64352E4D2E31362F6633782E7834544F2E7030504C3979512F494B57466C664C31486F69,1,NULL,NULL,1),
	 ('john','doe','john@mail.com',0x243279243130247A7870777971656D4467332F79454A4B69577476692E5A76437032446877775957324C334942586B38772E6475636E30704F575779,1,NULL,NULL,1),
	 ('leon',NULL,'leon@mail.com',0x243279243130244365393436674771356C6F545861705458553436482E70353842732F42765864355A4C6A494573505831385A424C5A6E6A58623636,1,NULL,NULL,1);

INSERT INTO paymybuddytest.authorities (name) VALUES
	 ('ROLE_ADMIN'),
	 ('ROLE_USER');

INSERT INTO paymybuddytest.user_authority (user_id,authority_id) VALUES
	 (1,1),
	 (1,2),
	 (2,2),
	 (3,2),
	 (4,2),
	 (5,2);

INSERT INTO paymybuddytest.accounts (currency_iso,balance,user_id) VALUES
	 ('EUR',1231330.545,1),
	 ('EUR',123.123,2),
	 ('EUR',1000.000,3),
	 ('EUR',345.000,4),
	 ('EUR',70.000,5);

INSERT INTO paymybuddytest.transactions (amount,description,from_account,to_account) VALUES
	 (22.23,'no description',1,2),
	 (125.0,'some description',1,2),
	 (6500.0,'other description',2,1),
	 (1337.0,NULL,2,1),
	 (224.0,'A NEW TRANSACTION',1,5),
	 (11.0,'A',1,2),
	 (22.0,'B',1,2),
	 (33.0,'C',1,4),
	 (44.0,'D',1,4),
	 (55.0,'E',1,5);
INSERT INTO paymybuddytest.transactions (amount,description,from_account,to_account) VALUES
	 (66.0,'F',1,5),
	 (77.0,'G',1,3),
	 (88.0,'H',1,2),
	 (99.0,'I',1,5),
	 (100.0,'J',2,4),
	 (110.0,'K',2,3),
	 (120.0,'L',2,4),
	 (130.0,'M',3,1),
	 (140.0,'N',3,2),
	 (150.0,'O',3,3);

INSERT INTO paymybuddytest.user_beneficiaries (user_id,beneficiary_id) VALUES
	 (1,2),
	 (4,2),
	 (1,3),
	 (2,3),
	 (1,4),
	 (2,4),
	 (1,5),
	 (3,5);