DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(320),
    name VARCHAR(100),
    CONSTRAINT uq_user_id UNIQUE(id),
    CONSTRAINT uq_user_email UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100),
    description VARCHAR(500),
    available BOOLEAN,
    user_id BIGINT,
    request_user_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_items_to_request FOREIGN KEY(request_user_id) REFERENCES users(id),
    CONSTRAINT uq_item_id UNIQUE(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR(50),
    CONSTRAINT fk_booking_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
    CONSTRAINT fk_booking_to_item FOREIGN KEY(item_id) REFERENCES items(id),
    UNIQUE(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(500),
    requester_id BIGINT,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requester_id) REFERENCES users(id),
    UNIQUE(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(500),
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP,
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id),
    CONSTRAINT fk_comments_to_item FOREIGN KEY(item_id) REFERENCES items(id),
    UNIQUE(id)
);
