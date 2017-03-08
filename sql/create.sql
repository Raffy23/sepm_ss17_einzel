-- Do not change this file!
-- This file does define some important parts of the Database

CREATE TABLE IF NOT EXISTS Box (
  boxid     INT PRIMARY KEY ,
  price     FLOAT,
  size      FLOAT,
  litter    VARCHAR(64),
  window    BOOL,
  indoor    BOOL,
  photo     VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS Invoice (
  invoiceID   INTEGER,
  reservation INTEGER REFERENCES Reservation(reservationID),

  PRIMARY KEY (invoiceID,reservation),
  UNIQUE (invoiceID)
);

CREATE TABLE IF NOT EXISTS Reservation (
  reservationID INTEGER,
  boxID     INTEGER REFERENCES Box(boxid),
  start     DATE,
  end       Date,
  customer  VARCHAR(128),
  horse     VARCHAR(128),
  price     FLOAT,

  UNIQUE (reservationID,boxID,start,end)
)