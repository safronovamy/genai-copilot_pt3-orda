-- Generate ONE SQL INSERT statement for H2 to seed EXACTLY 50 rows into table orders.
-- Schema: orders (id BIGINT auto, customer_name VARCHAR, status VARCHAR, amount DECIMAL(12,2), created_at TIMESTAMP)
-- Requirements:
-- - Insert exactly 50 rows (do not specify id).
-- - status must be one of: NEW, PAID, SHIPPED, CANCELLED.
-- - amount must be DECIMAL(12,2) with 2 decimal places.
-- - created_at must be in the past, within the last 60 days relative to 2026-01-23
--   (i.e., between 2025-11-24 and 2026-01-23), format 'YYYY-MM-DD HH:MM:SS' compatible with H2.
-- - Use realistic customer_name values, avoid apostrophes in names to keep SQL simple.
-- Output only SQL

INSERT INTO orders (customer_name, status, amount, created_at) VALUES
('Alice Johnson', 'NEW', 150.75, '2025-12-15 10:30:45'),
('Bob Smith', 'PAID', 200.00, '2025-11-30 14:20:10'),
('Charlie Brown', 'SHIPPED', 99.99, '2025-12-05 09:15:30'),
('Diana Prince', 'CANCELLED', 250.50, '2025-12-20 16:45:00'),
('Ethan Hunt', 'NEW', 300.00, '2026-01-10 11:00:00'),
('Fiona Gallagher', 'PAID', 120.25, '2025-12-25 13:30:15'),
('George Martin', 'SHIPPED', 450.75, '2025-11-28 08:50:20'),
('Hannah Baker', 'CANCELLED', 80.00, '2025-12-18 17:10:05'),
('Ian Somerhalder', 'NEW', 175.40, '2026-01-05 12:25:35'),
('Jenna Fischer', 'PAID', 220.60, '2025-12-02 15:55:45'),
('Kevin Hart', 'SHIPPED', 130.80, '2025-11-26 10:05:50'),
('Laura Palmer', 'CANCELLED', 90.90, '2025-12-22 14:40:25'),
('Michael Scott', 'NEW', 310.15, '2026-01-15 09:35:55'),
('Nina Dobrev', 'PAID', 140.70, '2025-12-08 11:45:30'),
('Oscar Isaac', 'SHIPPED', 260.85, '2025-11-29 16:20:40'),
('Pam Beesly', 'CANCELLED', 110.95, '2025-12-19 13:15:10'),
('Quentin Tarantino', 'NEW', 400.00, '2026-01-12 10:50:20'),
('Rachel Green', 'PAID', 230.30, '2025-12-03 14:05:35'),
('Sam Winchester', 'SHIPPED', 150.55, '2025-11-27 09:25:45'),
('Tina Fey', 'CANCELLED', 95.60, '2025-12-21 17:40:15'),
('Uma Thurman', 'NEW', 280.75, '2026-01-08 12:15:30'),
('Victor Stone', 'PAID', 160.20, '2025-12-06 15:30:50'),
('Wendy Darling', 'SHIPPED', 120.45, '2025-11-25 10:55:05'),
('Xander Harris', 'CANCELLED', 85.35, '2025-12-17 13:20:25'),
('Yara Shahidi', 'NEW', 350.90, '2026-01-18 11:40:40'),
('Zoe Saldana', 'PAID', 190.80, '2025-12-09 14:55:15'),
('Aaron Paul', 'SHIPPED', 140.10, '2025-11-30 09:10:30'),
('Betty Cooper', 'CANCELLED', 105.25, '2025-12-23 16:35:50'),
('Caleb Rivers', 'NEW', 320.60, '2026-01-14 10:20:05'),
('Donna Paulsen', 'PAID', 175.75, '2025-12-04 13:45:20'),
('Elliot Alderson', 'SHIPPED', 130.95, '2025-11-28 08:30:35'),
('Felicity Smoak', 'CANCELLED', 90.15, '2025-12-16 15:55:45'),
('Gina Linetti', 'NEW', 290.40, '2026-01-09 12:10:10'),
('Harvey Specter', 'PAID', 210.85, '2025-12-07 14:25:30'),
('Isabella Garcia', 'SHIPPED', 160.50, '2025-11-26 10:40:55'),
('Jack Sparrow', 'CANCELLED', 115.65, '2025-12-20 17:05:15'),
('Kara Danvers', 'NEW', 330.20, '2026-01-11 11:30:25'),
('Liam Neeson', 'PAID', 185.95, '2025-12-01 13:50:40'),
('Mia Wallace', 'SHIPPED', 145.30, '2025-11-29 09:15:55'),
('Nancy Wheeler', 'CANCELLED', 100.40, '2025-12-22 16:30:10'),
('Oliver Queen', 'NEW', 360.75, '2026-01-16 10:45:20'),
('Paige Matthews', 'PAID', 195.60, '2025-12-10 14:00:35'),
('Quinn Fabray', 'SHIPPED', 150.85, '2025-11-27 08:20:50'),
('Ron Swanson', 'CANCELLED', 110.10, '2025-12-18 15:40:05'),
('Samantha Carter', 'NEW', 340.95, '2026-01-07 12:05:15'),
('Tommy Shelby', 'PAID', 200.25, '2025-12-05 13:20:30'),
('Ulysses Grant', 'SHIPPED', 155.50, '2025-11-25 09:35:45'),
('Violet Baudelaire', 'CANCELLED', 95.75, '2025-12-21 16:55:00'),
('Walter White', 'NEW', 370.80, '2026-01-19 11:15:10'),
('Xena Warrior', 'PAID', 180.40, '2025-12-08 14:30:25');