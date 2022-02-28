INSERT INTO paymybuddytest.users (first_name,last_name,email,password,enabled) VALUES
	 ('admin',NULL,'admin@email.com',0x243279243130243774486A6D515873414937584F6957732F6C457371654E3736676A544E5544524B62663142746E5A4C6646476734626136704B7175,1),
	 ('user',NULL,'user@email.com',0x24327924313024455632454330667341564E737256712E63694E364E4F56434B506D4B5A3863536431496B38427A38635A666A394E754E6A79666F32,1),
	 ('james','smith','james@mail.com',0x243279243130245A465254595A6F493254723361504B334F64352E4D2E31362F6633782E7834544F2E7030504C3979512F494B57466C664C31486F69,1),
	 ('john','doe','john@mail.com',0x243279243130247A7870777971656D4467332F79454A4B69577476692E5A76437032446877775957324C334942586B38772E6475636E30704F575779,1),
	 ('leon',NULL,'leon@mail.com',0x243279243130246268776B516A6B3943794E7A522F485A77736C2E342E4A504F2E6A6E56332F70554D4D6D50432F626F6D5868636730584551426971,1);

INSERT INTO paymybuddytest.authorities (name) VALUES
	 ('ROLE_ADMIN'),
	 ('ROLE_USER');

INSERT INTO paymybuddytest.user_authority (user_id,authority_id) VALUES
	 (1,1),
	 (1,2),
	 (2,2);

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
	 (1337.0,NULL,2,1);

INSERT INTO paymybuddytest.users_beneficiaries (user_id,beneficiary_id) VALUES
	 (1,2),
	 (4,2),
	 (1,3),
	 (2,3),
	 (1,4),
	 (2,4),
	 (1,5),
	 (3,5);