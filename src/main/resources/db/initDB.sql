DROP TABLE IF EXISTS user_roles;
drop table if exists user_meal;
DROP TABLE IF EXISTS users;
drop table if exists meals;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
  id               INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
  name             VARCHAR                 NOT NULL,
  email            VARCHAR                 NOT NULL,
  password         VARCHAR                 NOT NULL,
  registered       TIMESTAMP DEFAULT now() NOT NULL,
  enabled          BOOL DEFAULT TRUE       NOT NULL,
  calories_per_day INTEGER DEFAULT 2000    NOT NULL
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE user_roles
(
  user_id INTEGER NOT NULL,
  role    VARCHAR,
  CONSTRAINT user_roles_idx UNIQUE (user_id, role),
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

create table meals
(
    id          integer primary key default nextval('global_seq'),
    dateTime    timestamp   not null,
    description varchar     not null,
    calories    integer     not null
);

create table user_meal
(
    user_id integer not null,
    meal_id integer not null,
    constraint user_meal_idx unique (user_id, meal_id),
    foreign key (user_id) references users(id) on delete cascade,
    foreign key (meal_id) references meals(id) on delete cascade
);