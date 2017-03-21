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
  boxid     INT AUTO_INCREMENT PRIMARY KEY,

  -- Box Data:
  price     FLOAT CONSTRAINT box_price_constraint CHECK (price>=0),
  size      FLOAT CONSTRAINT box_size_constraint CHECK(size>=0),
  litter    VARCHAR(64),
  window    BOOL,
  indoor    BOOL,

  -- The is in the Blob storage this is the Identifier
  photo     VARCHAR(64),

  -- For stale Boxes there is a deletion flag
  -- Boxes might be needed for some Invoices
  -- so we can't delete them
  deleted BOOL DEFAULT FALSE
);

-- Creates the main Table where the Reservations are stored
CREATE TABLE IF NOT EXISTS Reservation (

  -- Is needed for the INDEX, no data
  --wtf_fuck_h2_auto_indices INTEGER AUTO_INCREMENT CONSTRAINT h2_wtf PRIMARY KEY ,
  reservationID INTEGER NOT NULL CONSTRAINT valid_reservation CHECK (reservationID>=0),

  -- Data of the Reservation:
  boxID     INTEGER CONSTRAINT boxid_fk REFERENCES Box(boxid),
  start     DATE NOT NULL,
  end       DATE NOT NULL,
  customer  VARCHAR(128) NOT NULL,
  horse     VARCHAR(128) NOT NULL,
  price     FLOAT CONSTRAINT reservation_price_constraint CHECK (price>=0),

  -- A flag for Reservations which are already build to Invoices
  alreadyInvoice BOOL DEFAULT FALSE,

  CONSTRAINT unique_ids UNIQUE (reservationID,boxID),
  CONSTRAINT unique_box_data UNIQUE (boxID,start,end)
);

-- EOF