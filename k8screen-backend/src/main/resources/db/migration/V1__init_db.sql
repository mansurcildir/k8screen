-- Role table
CREATE TABLE "roles" (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- User table
CREATE TABLE "users" (
    id VARCHAR(26) PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    picture TEXT,
    config VARCHAR(255)
);

-- Config table
CREATE TABLE "configs" (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id VARCHAR(26) NOT NULL,
    CONSTRAINT unique_user_id_name UNIQUE (user_id, name),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "users" (id) ON DELETE CASCADE
);

-- User-Roles relationship table
CREATE TABLE "user_roles" (
    user_id VARCHAR(26) NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "users" (id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES "roles" (id) ON DELETE CASCADE
);

INSERT INTO "roles" (id, name)
VALUES
(0, 'ADMIN'),
(1, 'USER');
