INSERT INTO paymybuddytest.users (first_name,last_name,email,password,enabled) VALUES
	 ('admin',NULL,'admin@email.com',0x243279243130243774486A6D515873414937584F6957732F6C457371654E3736676A544E5544524B62663142746E5A4C6646476734626136704B7175,1),
	 ('user',NULL,'user@email.com',0x24327924313024455632454330667341564E737256712E63694E364E4F56434B506D4B5A3863536431496B38427A38635A666A394E754E6A79666F32,1);

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