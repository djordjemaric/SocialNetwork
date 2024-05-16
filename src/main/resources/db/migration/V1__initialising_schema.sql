CREATE TABLE "Comment"
(
    "IdComment" integer NOT NULL,
    "IdPost" integer NOT NULL,
    "IdCommOwner" integer NOT NULL,
    "Text" varchar(2000)
);

CREATE TABLE "FriendRequest"
(
    "IdSender" integer NOT NULL,
    "IdReceiver" integer NOT NULL
);

CREATE TABLE "Friends"
(
    "IdFriend1" integer NOT NULL,
    "IdFriend2" integer NOT NULL
);

CREATE TABLE "Group"
(
    "IdGroup" integer NOT NULL,
    "IdAdmin" integer NOT NULL
);

CREATE TABLE "GroupMember"
(
    "IdMember" integer NOT NULL,
    "IdGroup" integer NOT NULL
);

CREATE TABLE "GroupRequest"
(
    "IdUser" integer NOT NULL,
    "IdGroup" integer NOT NULL
);

CREATE TABLE "Post"
(
    "IdPost" integer NOT NULL,
    "IsPublic" boolean,
    "Text" varchar(2000),
    "ImgUrl" varchar(2000),
    "IdOwner" integer NOT NULL
);

CREATE TABLE "PostGroup"
(
    "IdPost" integer NOT NULL,
    "IdGroup" integer NOT NULL
);

CREATE TABLE "Reply"
(
    "IdComment" integer NOT NULL,
    "IdPost" integer NOT NULL,
    "IdCommOwner" integer NOT NULL,
    "Text" varchar(2000),
    "IdReplyOwner" integer NOT NULL
);

CREATE TABLE "User"
(
    "IdUser" integer NOT NULL,
    "Username" varchar(30),
    "Password" varchar(30)
);

ALTER TABLE "Comment"
    ADD CONSTRAINT "XPKComment" PRIMARY KEY ("IdComment", "IdPost", "IdCommOwner");

ALTER TABLE "FriendRequest"
    ADD CONSTRAINT "XPKFriendRequest" PRIMARY KEY ("IdSender", "IdReceiver");

ALTER TABLE "Friends"
    ADD CONSTRAINT "XPKFriends" PRIMARY KEY ("IdFriend1", "IdFriend2");

ALTER TABLE "Group"
    ADD CONSTRAINT "XPKGroup" PRIMARY KEY ("IdGroup");

ALTER TABLE "GroupMember"
    ADD CONSTRAINT "XPKGroupMember" PRIMARY KEY ("IdMember", "IdGroup");

ALTER TABLE "GroupRequest"
    ADD CONSTRAINT "XPKGroupRequest" PRIMARY KEY ("IdUser", "IdGroup");

ALTER TABLE "Post"
    ADD CONSTRAINT "XPKPost" PRIMARY KEY ("IdPost");

ALTER TABLE "PostGroup"
    ADD CONSTRAINT "XPKPostGroup" PRIMARY KEY ("IdPost");

ALTER TABLE "Reply"
    ADD CONSTRAINT "XPKReply" PRIMARY KEY ("IdComment", "IdPost", "IdCommOwner", "IdReplyOwner");

ALTER TABLE "User"
    ADD CONSTRAINT "XPKUser" PRIMARY KEY ("IdUser");

ALTER TABLE "Comment"
    ADD CONSTRAINT "R_25" FOREIGN KEY ("IdPost") REFERENCES "Post"("IdPost")
        ON DELETE CASCADE;

ALTER TABLE "Comment"
    ADD CONSTRAINT "R_26" FOREIGN KEY ("IdCommOwner") REFERENCES "User"("IdUser");

ALTER TABLE "FriendRequest"
    ADD CONSTRAINT "R_13" FOREIGN KEY ("IdSender") REFERENCES "User"("IdUser");

ALTER TABLE "FriendRequest"
    ADD CONSTRAINT "R_14" FOREIGN KEY ("IdReceiver") REFERENCES "User"("IdUser");

ALTER TABLE "Friends"
    ADD CONSTRAINT "R_15" FOREIGN KEY ("IdFriend1") REFERENCES "User"("IdUser");

ALTER TABLE "Friends"
    ADD CONSTRAINT "R_16" FOREIGN KEY ("IdFriend2") REFERENCES "User"("IdUser");

ALTER TABLE "Group"
    ADD CONSTRAINT "R_6" FOREIGN KEY ("IdAdmin") REFERENCES "User"("IdUser");

ALTER TABLE "GroupMember"
    ADD CONSTRAINT "R_17" FOREIGN KEY ("IdMember") REFERENCES "User"("IdUser");

ALTER TABLE "GroupMember"
    ADD CONSTRAINT "R_18" FOREIGN KEY ("IdGroup") REFERENCES "Group"("IdGroup")
        ON DELETE CASCADE;

ALTER TABLE "GroupRequest"
    ADD CONSTRAINT "R_11" FOREIGN KEY ("IdUser") REFERENCES "User"("IdUser");

ALTER TABLE "GroupRequest"
    ADD CONSTRAINT "R_12" FOREIGN KEY ("IdGroup") REFERENCES "Group"("IdGroup")
        ON DELETE CASCADE;

ALTER TABLE "Post"
    ADD CONSTRAINT "R_19" FOREIGN KEY ("IdOwner") REFERENCES "User"("IdUser");

ALTER TABLE "PostGroup"
    ADD CONSTRAINT "R_31" FOREIGN KEY ("IdPost") REFERENCES "Post"("IdPost")
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE "PostGroup"
    ADD CONSTRAINT "R_41" FOREIGN KEY ("IdGroup") REFERENCES "Group"("IdGroup")
        ON DELETE CASCADE;

ALTER TABLE "Reply"
    ADD CONSTRAINT "R_29" FOREIGN KEY ("IdComment", "IdPost", "IdCommOwner") REFERENCES "Comment"("IdComment", "IdPost", "IdCommOwner")
        ON DELETE CASCADE;

ALTER TABLE "Reply"
    ADD CONSTRAINT "R_30" FOREIGN KEY ("IdReplyOwner") REFERENCES "User"("IdUser");
