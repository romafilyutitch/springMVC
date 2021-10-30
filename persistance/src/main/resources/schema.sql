CREATE TABLE tag (
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(50) NOT NULL
);

CREATE TABLE gift_certificate (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration INT NOT NULL,
    create_date DATE,
    last_update_date DATE
);

CREATE TABLE certificate_tag (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    certificate_id INT NOT NULL,
    tag_id INT NOT NULL,
    CONSTRAINT certificate_fk FOREIGN KEY (certificate_id)
    REFERENCES gift_certificate (id) ON DELETE CASCADE,
    CONSTRAINT tag_fk FOREIGN KEY (tag_id)
    REFERENCES tag (id) ON DELETE CASCADE
);

CREATE TABLE user (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) NOT NULL,
    surname varchar(50) NOT NULL
);

CREATE TABLE certificate_order (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cost DECIMAL(10,2) NOT NULL,
    date DATE,
    user_id INT NOT NULL,
    certificate_id INT NOT NULL,
    CONSTRAINT certificate_order_fk FOREIGN KEY (certificate_id)
    REFERENCES gift_certificate(id) ON DELETE CASCADE,
    CONSTRAINT user_fk FOREIGN KEY (user_id)
    REFERENCES user (id) ON DELETE CASCADE
);