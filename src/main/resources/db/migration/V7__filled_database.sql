BEGIN;

INSERT INTO "user" (email, user_sub)
VALUES
    ('filipnovakovic57@gmail.com', '83b43882-30e1-70c2-df08-b669ab416d0d'),
    ('filipnovakovic57.fn@gmail.com', 'a344c812-70e1-7075-fdca-57312eb82a1b'),
    ('test@testing.com', '23243872-1061-70ea-d260-64d0d023fd1b'),
    ('vica.ristic@gmail.com', '93246812-3021-704e-9c37-bf46100f22dc'),
    ('xanitev711@mcatag.com', 'f3841812-e0f1-7025-b7bc-ce67d7fb933e'),
    ('maricdjole0@gmail.com', '63a43852-d041-707c-7396-ef468444454e'),
    ('ivasusa04@gmail.com', '93c40862-0071-7021-f521-04f19a765ede'),
    ('ivanalukovic01@gmail.com', 'a3549892-5091-7088-db28-af414304aab9'),
    ('ivana01.lukovic@gmail.com', '93c40822-d001-7041-59fc-8b85701d2442'),
    ('zeljko.tanjevic@gmail.com', '53241812-7021-7093-ee72-adb5ea845b01');

COMMIT;


BEGIN;

INSERT INTO "group" (name, id_admin, is_public)
VALUES
    ('Public Group', 1, true),
    ('Private Group', 2, false);

COMMIT;


BEGIN;

INSERT INTO group_member (id_member, id_group)
VALUES
    (1, 1),
    (2, 1),
    (3, 2);

COMMIT;


BEGIN;

INSERT INTO group_request (id_user, id_group)
VALUES
    (1, 2);

COMMIT;


BEGIN;

INSERT INTO post (is_public, text, img_s3_key, id_owner, id_group)
VALUES
    (true, 'Hello World!', NULL, 1, 1),
    (false, 'Hello Group2!', NULL, 2, 2),
    (true, 'My first post!', NULL, 8, NULL),
    (true, 'Check out this photo!', NULL, 9, NULL),
    (true, 'My first post!', NULL, 7, NULL);
COMMIT;


BEGIN;

INSERT INTO comment (text, id_comm_owner, id_post)
VALUES
    ('Nice post!', 2, 1),
    ('Thanks for sharing.', 3, 2);

COMMIT;


BEGIN;

INSERT INTO reply (text, id_reply_owner, id_comment)
VALUES
    ('I agree!', 1, 1),
    ('You are welcome!', 2, 2);

COMMIT;


BEGIN;

INSERT INTO friend_request (id_sender, id_receiver)
VALUES
    (1, 2),
    (2, 3),
    (10, 4),
    (7, 4);

COMMIT;

BEGIN;

INSERT INTO friends (id_friend1, id_friend2)
VALUES
    (1, 2),
    (2, 3),
    (4, 5);

COMMIT;
