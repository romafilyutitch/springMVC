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
    REFERENCES gift_certificate (id),
    CONSTRAINT tag_fk FOREIGN KEY (tag_id)
    REFERENCES tag (id)
);