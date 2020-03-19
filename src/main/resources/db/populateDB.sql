DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM meals;
DELETE FROM user_meal;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password) VALUES
  ('User', 'user@yandex.ru', 'password'),
  ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id) VALUES
  ('ROLE_USER', 100000),
  ('ROLE_ADMIN', 100001);

insert into meals(datetime, description, calories) values
    ('2020-01-30 10:00', 'Завтрак', 500),
    ('2020-01-30 13:00', 'Обед', 1000),
    ('2020-01-30 20:00', 'Ужин', 500),
    ('2020-01-31 0:00', 'Еда на граничное значение', 100),
    ('2020-01-31 10:00', 'Завтрак', 1000),
    ('2020-01-31 13:00', 'Обед', 500),
    ('2020-01-31 20:00', 'Ужин', 410),
    ('2015-05-01 14:0', 'Админ ланч', 510),
    ('2015-05-01 21:0', 'Админ ужин', 1500);

insert into user_meal (user_id, meal_id) values
    (100000, 100002),
    (100000, 100003),
    (100000, 100004),
    (100000, 100005),
    (100000, 100006),
    (100000, 100007),
    (100000, 100008),
    (100001, 100009),
    (100001, 100010);