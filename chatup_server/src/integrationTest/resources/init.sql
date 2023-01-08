INSERT INTO users (user_id, first_name, last_name, username, password, status, is_enabled) values
                   (1, 'test','test', 'test.test.1', '$2y$10$bswyoyCaXnI0SnSqXmm91u/cXOTfugc5AOMjceG3rgA3LUOS8DS7y', 'n', true), -- p:test
                   (2, 'test','test', 'test.test.2', '$2y$10$bswyoyCaXnI0SnSqXmm91u/cXOTfugc5AOMjceG3rgA3LUOS8DS7y', 'n', true), -- p:12345678
                   (3, 'test','test', 'test.test.3', '$2y$10$bswyoyCaXnI0SnSqXmm91u/cXOTfugc5AOMjceG3rgA3LUOS8DS7y', 'n', true), -- p:test
                   (4, 'johny','mielony', 'test.test.4', '$2y$10$bswyoyCaXnI0SnSqXmm91u/cXOTfugc5AOMjceG3rgA3LUOS8DS7y', 'n', true); -- p:test
ALTER SEQUENCE user_sequence RESTART WITH 10;

INSERT INTO channels (channel_id, name, is_private, is_direct_message) VALUES
                        (1, 'Test1', false, false),
                        (2, 'Test2', false, false),
                        (3, 'Test3', false, false);

ALTER SEQUENCE channel_sequence RESTART WITH 5;


INSERT INTO channels_users(channel_id, user_id) values
                            (1, 1), (1, 2), (1, 3), (1, 4),
                            (2, 1), (2, 2), (2, 3), (2, 4),
                            (3, 1), (3, 2), (3, 3), (3, 4);

INSERT INTO messages(message_id, content, time_created, author_user_id, channel_id, is_deleted) values
                    (1, 'test test', '2005-04-02 21:37:0-00', 1, 1, false),
                    (2, 'test test', '2005-04-02 21:37:1-00', 2, 1, false),
                    (3, 'test test', '2005-04-02 21:37:2-00', 2, 1, false),
                    (4, 'test test', '2005-04-02 21:37:3-00', 3, 2, false),
                    (5, 'test test', '2005-04-02 21:37:4-00', 4, 2, false),
                    (6, 'test test', '2005-04-02 21:37:5-00', 2, 2, false),
                    (7, 'test test', '2005-04-02 21:37:6-00', 1, 2, false),
                    (8, 'test test', '2005-04-02 21:37:7-00', 3, 1, false),
                    (9, 'test test', '2005-04-02 21:37:8-00', 4, 1, false),
                    (10, 'test test', '2005-04-02 21:37:9-00', 2, 1, false),
                    (11, 'test test', '2005-04-02 21:37:10-00', 3, 3, false),
                    (12, 'test test', '2005-04-02 21:37:11-00', 1, 3, false),
                    (13, 'test test', '2005-04-02 21:37:12-00', 1, 3, false),
                    (14, 'test test', '2005-04-02 21:37:13-00', 3, 1, false),
                    (15, 'test test', '2005-04-02 21:37:14-00', 4, 1, false);

ALTER SEQUENCE message_sequence RESTART WITH 100;
