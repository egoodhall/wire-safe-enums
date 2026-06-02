--liquibase formatted sql

--changeset egoodhall:create-simple-string-table context:job
CREATE TABLE string_table (
  id INTEGER NOT NULL PRIMARY KEY,
  type TEXT NOT NULL
) STRICT;

--changeset egoodhall:create-simple-int-table context:job
CREATE TABLE int_table (
  id INTEGER NOT NULL PRIMARY KEY,
  type INTEGER NOT NULL
) STRICT;
