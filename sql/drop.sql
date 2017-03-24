-- @Author  : Raphael Ludwig
-- @Date    : 08.03.2017
-- @Version : v2

-- *****************************************************************
-- ** Do not change this file!                                    **
-- ** This file does define some important parts of the Database  **
-- *****************************************************************

-- *Warning*: this file does clear the Database!

-- First make sure we don't have any Data left in the Database
DELETE FROM RESERVATION;
DELETE FROM BOX;

-- Delete even the Tables so we should have an empty Database
DROP TABLE BOX;
DROP TABLE RESERVATION;