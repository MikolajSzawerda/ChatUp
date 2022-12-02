INSERT INTO users (user_id, first_name, last_name, username, password, status, is_enabled) values
                   (1, 'test','test', 'test.test.1', '$2y$10$bswyoyCaXnI0SnSqXmm91u/cXOTfugc5AOMjceG3rgA3LUOS8DS7y', 'n', true), -- p:test
                   (2, 'test','test', 'test.test.2', '$2y$10$bswyoyCaXnI0SnSqXmm91u/cXOTfugc5AOMjceG3rgA3LUOS8DS7y', 'n', true), -- p:12345678
                   (3, 'test','test', 'test.test.3', '$2y$10$bswyoyCaXnI0SnSqXmm91u/cXOTfugc5AOMjceG3rgA3LUOS8DS7y', 'n', true), -- p:test
                   (4, 'test','test', 'test.test.4', '$2y$10$bswyoyCaXnI0SnSqXmm91u/cXOTfugc5AOMjceG3rgA3LUOS8DS7y', 'n', true); -- p:test
ALTER SEQUENCE user_sequence RESTART WITH 10;