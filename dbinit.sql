/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [id]
      ,[country]
      ,[dob]
      ,[email]
      ,[firstname]
      ,[gender]
      ,[last_connection_date]
      ,[lastname]
      ,[membership_expiry_date]
      ,[password]
      ,[phone]
      ,[role]
      ,[sign_up_date]
      ,[status]
      ,[town]
      ,[username]
  FROM [td].[dbo].[player]
  

  INSERT INTO player ([country]
      ,[dob]
      ,[email]
      ,[firstname]
      ,[gender]
      ,[last_connection_date]
      ,[lastname]
      ,[membership_expiry_date]
      ,[password]
      ,[phone]
      ,[role]
      ,[sign_up_date]
      ,[status]
      ,[town]
      ,[username]) 
	  VALUES ('', '1980-10-10', 'ccc@gmail.com', 'Charlie', 1, NULL, 'brown', NULL, '', '9876543', 0, '', 2, '', 'ccccc')

  INSERT INTO player ([country]
      ,[dob]
      ,[email]
      ,[firstname]
      ,[gender]
      ,[last_connection_date]
      ,[lastname]
      ,[membership_expiry_date]
      ,[password]
      ,[phone]
      ,[role]
      ,[sign_up_date]
      ,[status]
      ,[town]
      ,[username]) 
	  VALUES ('', '1980-10-10', 'aaa@gmail.com', 'Alice', 1, NULL, 'wonderland', NULL, '', '123456', 0, '', 2, '', 'aaaaaa')

	 

INSERT INTO ladder_subscription (rank, status, ladder_id, player_id) VALUES (1, 1, 1, 2)
INSERT INTO ladder_subscription (rank, status, ladder_id, player_id) VALUES (2, 1, 1, 3)