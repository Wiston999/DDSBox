CREATE TABLE IF NOT EXISTS "configuration" (
    "id" INTEGER PRIMARY KEY,
    "config_name" TEXT NOT NULL UNIQUE,
    "content" TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS "files" (
    "id" INTEGER PRIMARY KEY,
    "file_path" TEXT UNIQUE,
    "owner" TEXT,
    "completed" INTEGER,
    "hash" TEXT NOT NULL,
    "version" INTEGER,
    "segments" TEXT,
    "size" INTEGER,
    "last_update" INTEGER,
    "id_folder" INTEGER,
    "isDir" INTEGER
);

CREATE TABLE IF NOT EXISTS shared_folder (
    "id" INTEGER,
    "path" TEXT UNIQUE,
    "name" TEXT,
    "type" INTEGER,
    "permission" INTEGER,
    "owner" TEXT,
    "uuid" TEXT PRIMARY KEY,
    "suscribed" INTEGER,
    "persistence" INTEGER,
    "key" TEXT
);

CREATE TABLE IF NOT EXISTS users (
    "id" INTEGER,
    "uuid" TEXT PRIMARY KEY,
    "username" TEXT,
    "realname" TEXT,
    "email" TEXT,
    "publickey" TEXT,
    "privatekey" TEXT,
    "online" INTEGER
);

CREATE TABLE IF NOT EXISTS folders_users (
    "id_folder" TEXT REFERENCES shared_folder(uuid) ON DELETE CASCADE,
    "id_user" TEXT REFERENCES users(uuid) ON DELETE CASCADE,
    "permission" INTEGER,
    PRIMARY KEY ("id_folder", "id_user")
);