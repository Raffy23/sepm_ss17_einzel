-- @Author  : Raphael Ludwig
-- @Date    : 08.03.2017
-- @Version : v2

-- *****************************************************************
-- ** Do not change this file!                                    **
-- ** This file does define some important parts of the Database  **
-- *****************************************************************

-- Creates the main Table where Boxes are stored
CREATE TABLE IF NOT EXISTS Box (

  -- Is needed for the INDEX
  boxid     INT PRIMARY KEY ,

  -- Box Data:
  price     FLOAT,
  size      FLOAT,
  litter    VARCHAR(64),
  window    BOOL,
  indoor    BOOL,

  -- The is in the Blob storage this is the Identifier
  photo     VARCHAR(64),

  -- For stale Boxes there is a deletion flag
  -- Boxes might be needed for some Invoices
  -- so we can't delete them
  deleted BOOL
);

-- Creates the main Table where the Reservations are stored
CREATE TABLE IF NOT EXISTS Reservation (

  -- Is needed for the INDEX, no data
  reservationID INTEGER,

  -- Data of the Reservation:
  boxID     INTEGER REFERENCES Box(boxid),
  start     DATE,
  end       Date,
  customer  VARCHAR(128),
  horse     VARCHAR(128),
  price     FLOAT,

  -- A flag for Reservations which are already build to Invoices
  alreadyInvoice BOOL,

  UNIQUE (reservationID,boxID,start,end)
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

-- EOF