INSERT INTO tag(id, name)  values (1, 'spotify');
INSERT INTO tag(id, name)  values (2, 'music');
INSERT INTO tag(id, name)  values (3, 'art');

INSERT INTO gift_certificate(id, name, description, price, duration, create_date, last_update_date) values (1, 'free music listen certificate', 'spotify free music listening', 200.50, 20, null, null);

INSERT INTO certificate_tag(certificate_id, tag_id) values(1, 1);
INSERT INTO certificate_tag(certificate_id, tag_id) values(1, 2);
INSERT INTO certificate_tag(certificate_id, tag_id) values(1, 3);

INSERT INTO user(id, username, password) values (1, 'user', 'test');

INSERT INTO certificate_order(id, cost, date, user_id, certificate_id) values(1, 200.50, null, 1, 1);

