-- @Author  : Raphael Ludwig
-- @Date    : 08.03.2017
-- @Version : v1

-- *****************************************************************
-- ** Do not change this file!                                    **
-- ** This file does define some important parts of the Database  **
-- *****************************************************************

-- Creates the main Table where Boxes are stored
CREATE TABLE IF NOT EXISTS Box (
  boxid     INT PRIMARY KEY ,
  price     FLOAT,
  size      FLOAT,
  litter    VARCHAR(64),
  window    BOOL,
  indoor    BOOL,
  photo     VARCHAR(64)
);

-- Creates the main Table where the Invoices are stored
-- The Invoice is calculated directly from the Reservation
-- in the Program
CREATE TABLE IF NOT EXISTS Invoice (
  invoiceID   INTEGER,
  reservation INTEGER REFERENCES Reservation(reservationID),

  PRIMARY KEY (invoiceID,reservation),
  UNIQUE (invoiceID)
);

-- Creates the main Table where the Reservartions are stored
-- This table only weakly links to a Box and directly stores
-- the price since it is mainly used for historical data and
-- Boxes might not exist that long (Some Law enforces us the
-- save this long enough)
CREATE TABLE IF NOT EXISTS Reservation (
  reservationID INTEGER,

  -- ID not linked in database due delete anomaly
  boxID     INTEGER,
  start     DATE,
  end       Date,
  customer  VARCHAR(128),
  horse     VARCHAR(128),
  price     FLOAT,

  UNIQUE (reservationID,boxID,start,end)
)