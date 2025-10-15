INSERT INTO tb_user (name, email, password)
SELECT 'Alex', 'alex@gmail.com', '$2a$10$o2wJjk9Ek44qdktVolS2nOadSFB57O.cP62yelzXYnLZDJhOcrCcC'
WHERE NOT EXISTS (SELECT 1 FROM tb_user WHERE email = 'alex@gmail.com');

INSERT INTO tb_user (name, email, password)
SELECT 'Maria', 'maria@gmail.com', '$2a$10$o2wJjk9Ek44qdktVolS2nOadSFB57O.cP62yelzXYnLZDJhOcrCcC'
WHERE NOT EXISTS (SELECT 1 FROM tb_user WHERE email = 'maria@gmail.com');

INSERT INTO tb_role (authority)
SELECT 'ROLE_OPERATOR'
WHERE NOT EXISTS (SELECT 1 FROM tb_role WHERE authority = 'ROLE_OPERATOR');

INSERT INTO tb_role (authority)
SELECT 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM tb_role WHERE authority = 'ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id)
SELECT 1, 1
WHERE NOT EXISTS (SELECT 1 FROM tb_user_role WHERE user_id = 1 AND role_id = 1);

INSERT INTO tb_user_role (user_id, role_id)
SELECT 2, 1
WHERE NOT EXISTS (SELECT 1 FROM tb_user_role WHERE user_id = 2 AND role_id = 1);

INSERT INTO tb_user_role (user_id, role_id)
SELECT 2, 2
WHERE NOT EXISTS (SELECT 1 FROM tb_user_role WHERE user_id = 2 AND role_id = 2);


---- Update password if user exists
--UPDATE tb_user
--SET password = '$2a$10$NEW_HASH'
--WHERE email = 'alex@gmail.com';
--
---- Insert if missing
--INSERT INTO tb_user (name, email, password)
--SELECT 'Alex', 'alex@gmail.com', '$2a$10$NEW_HASH'
--WHERE NOT EXISTS (SELECT 1 FROM tb_user WHERE email = 'alex@gmail.com');