INSERT INTO MEMBER (mem_email, mem_pw, mem_tel, mem_nn, mem_type, mem_created_at, mem_is_banned)
VALUES
    ('admin@example.com', '$2a$10$hPldukYZl8sXzWjO1Qv5VOzRVFvvH.SQpG3hQf9mrPQQ.DxPmjD3e', '010-1234-5678', 'Admin', 1, NOW(), 0), -- 비밀번호: admin123
    ('user@example.com', '$2a$10$9GmtFXDzKa1CZ1ZPDbQaEu4.j4jOqTKNdC2XPfiomNysbFrvXwRPK', '010-9876-5432', 'User', 0, NOW(), 0); -- 비밀번호: user123
