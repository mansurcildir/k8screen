-- User table
CREATE TABLE "user" (
    id                   BIGSERIAL                   NOT NULL,
    uuid                 UUID                        NOT NULL,
    username             VARCHAR(255)                NOT NULL,
    password             VARCHAR(255),
    email                VARCHAR(255)                NOT NULL,
    active_config        VARCHAR(255),
    stripe_customer_id   UUID,
    subscription_plan_id BIGINT                      NOT NULL,
    deleted              BOOLEAN DEFAULT FALSE       NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at           TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT ch_user CHECK (id > 0),
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_uuid UNIQUE (uuid)
);

CREATE TABLE IF NOT EXISTS account (
    id                   BIGSERIAL                   NOT NULL,
    uuid                 UUID                        NOT NULL,
    account_type         VARCHAR(256)                NOT NULL,
    subject_id           VARCHAR(256),
    username             VARCHAR(256),
    email                VARCHAR(256),
    user_id              BIGINT NOT NULL,
    avatar_url           TEXT,
    deleted              BOOLEAN DEFAULT FALSE       NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at           TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT ch_account CHECK ((id > 0)),
    CONSTRAINT ch_account__user_id CHECK ((user_id > 0)),
    CONSTRAINT pk_account PRIMARY KEY (id),
    CONSTRAINT fk_account__user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ux_account__account_type_subject_id
    ON account (account_type, subject_id)
    WHERE deleted = false;

-- role table
CREATE TABLE role (
    id   BIGSERIAL    NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT ch_role CHECK (id > 0),
    CONSTRAINT pk_role PRIMARY KEY (id),
    CONSTRAINT uq_role_name UNIQUE (name)
);

-- user_role relationship table
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
);

-- insert default roles
INSERT INTO role (name)
VALUES
  ('ADMIN'),
  ('USER');

-- refresh_token table
CREATE TABLE refresh_token (
    id         BIGSERIAL                   NOT NULL,
    uuid       UUID                        NOT NULL,
    token      TEXT                        NOT NULL,
    user_id    BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT ch_refresh_token CHECK (id > 0),
    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token_uuid UNIQUE (uuid),
    CONSTRAINT uq_refresh_token_token UNIQUE (token),
    CONSTRAINT fk_refresh_token_user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

-- config table
CREATE TABLE config (
    id         BIGSERIAL                   NOT NULL,
    uuid       UUID                        NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    user_id    BIGINT                      NOT NULL,
    deleted    BOOLEAN DEFAULT FALSE       NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT ch_config CHECK (id > 0),
    CONSTRAINT pk_config PRIMARY KEY (id),
    CONSTRAINT uq_config_uuid UNIQUE (uuid),
    CONSTRAINT fk_config_user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ux_config_user_id_name
ON config (user_id, name)
WHERE deleted = false;

CREATE UNIQUE INDEX ux_user_username
ON "user" (username)
WHERE deleted = false;

-- subscription_plan table
CREATE TABLE subscription_plan (
    id               BIGSERIAL    NOT NULL,
    name             VARCHAR(255) NOT NULL,
    max_config_count BIGINT       NOT NULL,
    CONSTRAINT ch_subscription_plan CHECK (id > 0),
    CONSTRAINT pk_subscription_plan PRIMARY KEY (id),
    CONSTRAINT uq_subscription_plan_name UNIQUE (name)
);

INSERT INTO subscription_plan (name, max_config_count)
VALUES
  ('Free', 1),
  ('Standard', 5),
  ('Premium', 50);
