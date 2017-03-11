-- @Author  : Raphael Ludwig
-- @Date    : 08.03.2017
-- @Version : v1

-- *****************************************************************
-- ** Do not change this file!                                    **
-- ** This file does define some important parts of the Database  **
-- *****************************************************************

-- This file inserts test data into the database
INSERT INTO Box (price,SIZE,LITTER,WINDOW,INDOOR,PHOTO) VALUES (100,25,'Straw',true,false,NULL);
INSERT INTO Box (price,SIZE,LITTER,WINDOW,INDOOR,PHOTO) VALUES (25,5,'Sawdust',false,false,NULL);
INSERT INTO Box (price,SIZE,LITTER,WINDOW,INDOOR,PHOTO) VALUES (35,18.4,'Sawdust',false,true,NULL);
INSERT INTO Box (price,SIZE,LITTER,WINDOW,INDOOR,PHOTO) VALUES (250,38,'Straw',true,true,NULL);
INSERT INTO Box (price,SIZE,LITTER,WINDOW,INDOOR,PHOTO) VALUES (75,23.7,'Sawdust',false,true,NULL);

-- TODO: Insert some Invoices

-- TODO: Insert some Reservations
INSERT INTO RESERVATION(RESERVATIONID, BOXID, START, END, CUSTOMER, HORSE, PRICE, ALREADYINVOICE) VALUES (0,1,'2017-01-28','2017-01-29','Kunde 1','Pferd 1',38.2,FALSE );
INSERT INTO RESERVATION(RESERVATIONID, BOXID, START, END, CUSTOMER, HORSE, PRICE, ALREADYINVOICE) VALUES (0,2,'2017-01-28','2017-01-29','Kunde 1','Pferd 2',75,FALSE );
INSERT INTO RESERVATION(RESERVATIONID, BOXID, START, END, CUSTOMER, HORSE, PRICE, ALREADYINVOICE) VALUES (0,3,'2017-01-28','2017-01-29','Kunde 1','Pferd 3',160,FALSE );