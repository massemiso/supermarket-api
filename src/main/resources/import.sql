INSERT INTO sec_role (id, version, name, created_at) VALUES (1, 1, 'ADMIN', CURRENT_TIMESTAMP);
INSERT INTO sec_role (id, version, name, created_at) VALUES (2, 1, 'MANAGER', CURRENT_TIMESTAMP);
INSERT INTO sec_role (id, version, name, created_at) VALUES (3, 1, 'CASHIER', CURRENT_TIMESTAMP);

INSERT INTO sec_user (id, username, password, email, is_account_expired, is_account_locked, is_credentials_expired, created_at) VALUES (1, 'admin', '$2a$10$YyOCGmvzfX49UrogwUvvzOHA52k5YwHKYSc/YD1ubiV3.VqscdOfa', 'admin@supermarket.com', false, false, false, CURRENT_TIMESTAMP);
INSERT INTO sec_user (id, username, password, email, is_account_expired, is_account_locked, is_credentials_expired, created_at) VALUES (2, 'manager', '$2a$10$YyOCGmvzfX49UrogwUvvzOHA52k5YwHKYSc/YD1ubiV3.VqscdOfa', 'manager@supermarket.com', false, false, false, CURRENT_TIMESTAMP);
INSERT INTO sec_user (id, username, password, email, is_account_expired, is_account_locked, is_credentials_expired, created_at) VALUES (3, 'cashier', '$2a$10$YyOCGmvzfX49UrogwUvvzOHA52k5YwHKYSc/YD1ubiV3.VqscdOfa', 'cashier@supermarket.com', false, false, false, CURRENT_TIMESTAMP);

INSERT INTO sec_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sec_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO sec_user_role (user_id, role_id) VALUES (3, 3);
