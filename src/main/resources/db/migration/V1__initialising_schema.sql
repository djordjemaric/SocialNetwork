CREATE TABLE "Comment"
(
    "Id"                 SERIAL PRIMARY KEY,
    "IdPost"             integer  NOT NULL,
    "IdCommOwner"        integer  NOT NULL,
    "Text"               varchar(2000)
);

CREATE TABLE "FriendRequest"
(
    "Id"                 SERIAL PRIMARY KEY,
    "IdSender"           integer  NOT NULL,
    "IdReceiver"         integer  NOT NULL
);

CREATE TABLE "Friends"
(
    "Id"                 SERIAL PRIMARY KEY,
    "IdFriend1"          integer  NOT NULL,
    "IdFriend2"          integer  NOT NULL
);

CREATE TABLE "Group"
(
    "Id"                 SERIAL PRIMARY KEY,
    "IdAdmin"            integer  NOT NULL,
    "IsPublic"           boolean
);

CREATE TABLE "GroupMember"
(
    "Id"                 SERIAL PRIMARY KEY,
    "IdMember"           integer  NOT NULL,
    "IdGroup"            integer  NOT NULL
);

CREATE TABLE "GroupRequest"
(
    "Id"                 SERIAL PRIMARY KEY,
    "IdUser"             integer  NOT NULL,
    "IdGroup"            integer  NOT NULL
);

CREATE TABLE "Post"
(
    "Id"                 SERIAL PRIMARY KEY,
    "IsPublic"           boolean,
    "Text"               varchar(2000),
    "ImgUrl"             varchar(2000),
    "IdOwner"            integer  NOT NULL,
    "IdGroup"            integer
);


CREATE TABLE "Reply"
(
    "Id"                 SERIAL PRIMARY KEY,
    "Text"               varchar(2000),
    "IdReplyOwner"       integer  NOT NULL,
    "IdComment"          integer  NOT NULL
);

CREATE TABLE "User"
(
    "Id"                 SERIAL PRIMARY KEY,
    "Email"              varchar(100) NOT NULL
);

ALTER TABLE "Comment"
    ADD CONSTRAINT "R_25" FOREIGN KEY ("IdPost") REFERENCES "Post"("Id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "Comment"
    ADD CONSTRAINT "R_26" FOREIGN KEY ("IdCommOwner") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "FriendRequest"
    ADD CONSTRAINT "R_13" FOREIGN KEY ("IdSender") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "FriendRequest"
    ADD CONSTRAINT "R_14" FOREIGN KEY ("IdReceiver") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "Friends"
    ADD CONSTRAINT "R_15" FOREIGN KEY ("IdFriend1") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "Friends"
    ADD CONSTRAINT "R_16" FOREIGN KEY ("IdFriend2") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "Group"
    ADD CONSTRAINT "R_6" FOREIGN KEY ("IdAdmin") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "GroupMember"
    ADD CONSTRAINT "R_17" FOREIGN KEY ("IdMember") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "GroupMember"
    ADD CONSTRAINT "R_18" FOREIGN KEY ("IdGroup") REFERENCES "Group"("Id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "GroupRequest"
    ADD CONSTRAINT "R_11" FOREIGN KEY ("IdUser") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "GroupRequest"
    ADD CONSTRAINT "R_12" FOREIGN KEY ("IdGroup") REFERENCES "Group"("Id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "Post"
    ADD CONSTRAINT "R_19" FOREIGN KEY ("IdOwner") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "Post"
    ADD CONSTRAINT "R_20" FOREIGN KEY ("IdGroup") REFERENCES "Group"("Id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "Reply"
    ADD CONSTRAINT "R_29" FOREIGN KEY ("IdComment") REFERENCES "Comment"("Id")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "Reply"
    ADD CONSTRAINT "R_30" FOREIGN KEY ("IdReplyOwner") REFERENCES "User"("Id")
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE "User"
    ADD CONSTRAINT "R_31" UNIQUE ("Email");


