INSERT INTO tag  values (1, 'spotify');
INSERT INTO tag  values (2, 'music');
INSERT INTO tag  values (3, 'art');

INSERT INTO gift_certificate values (1, 'free music listen certificate', 'spotify free music listening', 200.50, 20, null, null);

INSERT INTO certificate_tag values(1, 1, 1);
INSERT INTO certificate_tag values(2, 1, 2);
INSERT INTO certificate_tag values(3, 1, 3);