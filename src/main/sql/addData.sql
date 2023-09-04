begin;

insert into "user" (name, email, token, password) values
    ('R1c4rdCo5t4', 'r1c4rdco5t4@gmail.com', 'a7943c1e-46e7-47f7-b8f6-3f7a92ca96ed', '12345678'),
    ('wartuga', 'wartuga@gmail.com', 'da42c0fb-5ae6-4db0-ac6f-4a117bf7226e', '12345678');

insert into board (name, description) values
    ('University Tasks', 'Board with my college tasks'),
    ('Daily Tasks', 'Board with my daily tasks');

insert into userboard (userId, boardId) values
    (1, 1),
    (2, 1),
    (1, 2);

insert into list (name, boardId, index) values
    ('To-Do', 1, 100),
    ('Doing', 1, 200),
    ('Done', 1, 300);

insert into card (name, description, dueDate, listId, index) values
    ('Code', 'Code for 1h', null, 3, 100),
    ('Study for exam', 'Study for calculus exam', '2024-03-08T17:01:32', 1, 200),
    ('Go to gym', 'Workout', null, 1, 300);

commit;