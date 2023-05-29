CREATE TYPE event_type AS ENUM ('PHOTOGRAPHY', 'FILM', 'MARKETING', 'OTHER');

CREATE TYPE event_status AS ENUM ('TODO', 'IN_PROGRESS', 'REVIEW', 'DONE');

CREATE SEQUENCE event_id_generation;

CREATE TABLE Event(
  event_id int DEFAULT nextval('event_id_generation') PRIMARY KEY,
  client_id int NOT NULL,
  name text,
  type event_type,
  date date,
  start_time time,
  duration int,
  location text,
  production_manager_id int,
  crew_members int,
  status event_status,
  FOREIGN KEY (client_id) REFERENCES Client(client_id)
);

CREATE SEQUENCE account_id_generation;

CREATE TABLE Account(
  account_id int DEFAULT nextval('account_id_generation') PRIMARY KEY,
  username text,
  password text,
  email_address text,
  admin bool
);

CREATE TYPE department AS ENUM('EVENT_PHOTOGRAPHY', 'EVENT_FILMMAKING', 'CLUB_PHOTOGRAPHY', 'MARKETING');

CREATE TABLE Crew_member(
  user_id int PRIMARY KEY,
  department department,
  FOREIGN KEY (user_id) REFERENCES Account(account_id)
);

CREATE TABLE Administrator(
  user_id int PRIMARY KEY,
  FOREIGN KEY (user_id) REFERENCES Account(account_id)
);

CREATE SEQUENCE announcement_id_generation;

CREATE TABLE Announcement(
  announcement_id int DEFAULT nextval('announcement_id_generation') PRIMARY KEY,
  announcer int NOT NULL,
  title text,
  body text,
  datetime timestamp with time zone,
  FOREIGN KEY (announcer) REFERENCES Administrator(user_id)
);

CREATE SEQUENCE client_id_generation;

CREATE TABLE Client(
  client_id int DEFAULT nextval('client_id_generation') PRIMARY KEY,
  first_name text,
  last_name text,
  email text,
  phone_number text
);

CREATE TABLE Enrolls(
  event_id int,
  crew_id int,
  PRIMARY KEY (event_id, crew_id),
  FOREIGN KEY (event_id) REFERENCES Event(event_id),
  FOREIGN KEY (crew_id) REFERENCES Crew_member(user_id)
);


