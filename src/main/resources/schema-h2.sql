CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        description VARCHAR(255),
                                        created TIMESTAMP NOT NULL,
                                        CONSTRAINT requests_user_fkey FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     description VARCHAR(255),
                                     available BOOLEAN NOT NULL,
                                     owner_id BIGINT NOT NULL,
                                     request_id BIGINT,
                                     CONSTRAINT items_owner_fkey FOREIGN KEY (owner_id) REFERENCES users(id),
                                     CONSTRAINT items_request_fkey FOREIGN KEY (request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        item_id BIGINT NOT NULL,
                                        start_date TIMESTAMP NOT NULL,
                                        end_date TIMESTAMP NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        booked BOOLEAN NOT NULL,
                                        status VARCHAR(127) NOT NULL DEFAULT 'WAITING',
                                        CONSTRAINT bookings_item_fkey FOREIGN KEY (item_id) REFERENCES items(id),
                                        CONSTRAINT bookings_user_fkey FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        text VARCHAR(255) NOT NULL,
                                        item_id BIGINT NOT NULL,
                                        author_id BIGINT NOT NULL,
                                        created TIMESTAMP NOT NULL,
                                        CONSTRAINT comments_item_fkey FOREIGN KEY (item_id) REFERENCES items(id),
                                        CONSTRAINT comments_user_fkey FOREIGN KEY (author_id) REFERENCES users(id)
);
