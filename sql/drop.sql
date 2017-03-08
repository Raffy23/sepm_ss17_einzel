-- @Author  : Raphael Ludwig
-- @Date    : 08.03.2017
-- @Version : v1

-- *****************************************************************
-- ** Do not change this file!                                    **
-- ** This file does define some important parts of the Database  **
-- *****************************************************************

-- *Warning*: this file does clear the Database!

-- First make sure we don't have any Data left in the Database
DELETE FROM BOX;
DELETE FROM INVOICE;
DELETE FROM RESERVATION;

-- Delete even the Tables so we should have an empty Database
DROP TABLE BOX;
DROP TABLE INVOICE;
DROP TABLE RESERVATION;