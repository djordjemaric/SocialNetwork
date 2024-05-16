CREATE TABLE "comment"
(
    "id"                 SERIAL PRIMARY KEY,
    "id_post"             integer  NOT NULL,
    "id_comm_owner"        integer  NOT NULL,
    "text"               varchar(2000)
);

CREATE TABLE "friend_request"
(
    "id"                 SERIAL PRIMARY KEY,
    "id_sender"           integer  NOT NULL,
    "id_receiver"         integer  NOT NULL
);

CREATE TABLE "friends"
(
    "id"                 SERIAL PRIMARY KEY,
    "id_friend1"          integer  NOT NULL,
    "id_friend2"          integer  NOT NULL
);

CREATE TABLE "group"
(
    "id"                 SERIAL PRIMARY KEY,
    "id_admin"            integer  NOT NULL,
    "is_public"           boolean
);

CREATE TABLE "group_member"
(
    "id"                 SERIAL PRIMARY KEY,
    "id_member"           integer  NOT NULL,
    "id_group"            integer  NOT NULL
);

CREATE TABLE "group_request"
(
    "id"                 SERIAL PRIMARY KEY,
    "id_user"             integer  NOT NULL,
    "id_group"            integer  NOT NULL
);

CREATE TABLE "post"
(
    "id"                 SERIAL PRIMARY KEY,
    "is_public"           boolean,
    "text"               varchar(2000),
    "img_url"             varchar(2000),
    "id_owner"            integer  NOT NULL,
    "id_group"            integer
);


CREATE TABLE "reply"
(
    "id"                 SERIAL PRIMARY KEY,
    "text"               varchar(2000),
    "id_reply_owner"       integer  NOT NULL,
    "id_comment"          integer  NOT NULL
);

CREATE TABLE "user"
(
    "id"                 SERIAL PRIMARY KEY,
    "email"              varchar(100) NOT NULL
);

ALTER TABLE "comment"
    ADD CONSTRAINT "r_25" FOREIGN KEY ("id_post") REFERENCES "post"("id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "comment"
    ADD CONSTRAINT "r_26" FOREIGN KEY ("id_comm_owner") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "friend_request"
    ADD CONSTRAINT "r_13" FOREIGN KEY ("id_sender") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "friend_request"
    ADD CONSTRAINT "r_14" FOREIGN KEY ("id_receiver") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "friends"
    ADD CONSTRAINT "r_15" FOREIGN KEY ("id_friend1") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "friends"
    ADD CONSTRAINT "r_16" FOREIGN KEY ("id_friend2") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "group"
    ADD CONSTRAINT "r_6" FOREIGN KEY ("id_admin") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "group_member"
    ADD CONSTRAINT "r_17" FOREIGN KEY ("id_member") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "group_member"
    ADD CONSTRAINT "r_18" FOREIGN KEY ("id_group") REFERENCES "group"("id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "group_request"
    ADD CONSTRAINT "r_11" FOREIGN KEY ("id_user") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "group_request"
    ADD CONSTRAINT "r_12" FOREIGN KEY ("id_group") REFERENCES "group"("id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "post"
    ADD CONSTRAINT "r_19" FOREIGN KEY ("id_owner") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "post"
    ADD CONSTRAINT "r_20" FOREIGN KEY ("id_group") REFERENCES "group"("id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "reply"
    ADD CONSTRAINT "r_29" FOREIGN KEY ("id_comment") REFERENCES "comment"("id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "reply"
    ADD CONSTRAINT "r_30" FOREIGN KEY ("id_reply_owner") REFERENCES "user"("id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "user"
    ADD CONSTRAINT "r_31" UNIQUE ("email");


