-- Kategoria "Clothes"
INSERT INTO categories (id, name)
VALUES ('11111111-1111-1111-1111-111111111111', 'Clothes')
    ON CONFLICT (id) DO NOTHING;

-- Produkty (20 sztuk)
INSERT INTO products (id, category_id, name, approximate_price, delivery_price, description, info)
VALUES
    ('550e8400-e29b-41d4-a716-000000000001', '11111111-1111-1111-1111-111111111111', 'T-Shirt Basic', 25.00, 5.00, 'Classic cotton T-shirt', '{"size":["S","M","L","XL"],"material":"cotton"}'),
    ('550e8400-e29b-41d4-a716-000000000002', '11111111-1111-1111-1111-111111111111', 'T-Shirt Premium', 35.00, 5.00, 'Premium cotton T-shirt', '{"size":["S","M","L","XL"],"material":"cotton"}'),
    ('550e8400-e29b-41d4-a716-000000000003', '11111111-1111-1111-1111-111111111111', 'Jeans Slim Fit', 60.00, 7.50, 'Blue slim fit jeans', '{"size":["28","30","32","34"],"material":"denim"}'),
    ('550e8400-e29b-41d4-a716-000000000004', '11111111-1111-1111-1111-111111111111', 'Jeans Regular Fit', 55.00, 7.50, 'Regular fit blue jeans', '{"size":["28","30","32","34"],"material":"denim"}'),
    ('550e8400-e29b-41d4-a716-000000000005', '11111111-1111-1111-1111-111111111111', 'Hoodie Winter', 80.00, 10.00, 'Warm hoodie for winter', '{"size":["M","L","XL"],"material":"fleece"}'),
    ('550e8400-e29b-41d4-a716-000000000006', '11111111-1111-1111-1111-111111111111', 'Hoodie Summer', 65.00, 10.00, 'Light hoodie for summer', '{"size":["M","L","XL"],"material":"cotton"}'),
    ('550e8400-e29b-41d4-a716-000000000007', '11111111-1111-1111-1111-111111111111', 'Jacket Leather', 150.00, 12.00, 'Premium leather jacket', '{"size":["M","L","XL"],"material":"leather"}'),
    ('550e8400-e29b-41d4-a716-000000000008', '11111111-1111-1111-1111-111111111111', 'Jacket Denim', 90.00, 10.00, 'Classic denim jacket', '{"size":["M","L","XL"],"material":"denim"}'),
    ('550e8400-e29b-41d4-a716-000000000009', '11111111-1111-1111-1111-111111111111', 'Sweater Wool', 70.00, 8.00, 'Warm wool sweater', '{"size":["M","L","XL"],"material":"wool"}'),
    ('550e8400-e29b-41d4-a716-000000000010', '11111111-1111-1111-1111-111111111111', 'Sweater Cotton', 50.00, 5.00, 'Soft cotton sweater', '{"size":["M","L","XL"],"material":"cotton"}'),
    ('550e8400-e29b-41d4-a716-000000000011', '11111111-1111-1111-1111-111111111111', 'Shorts Summer', 30.00, 5.00, 'Light summer shorts', '{"size":["S","M","L","XL"],"material":"cotton"}'),
    ('550e8400-e29b-41d4-a716-000000000012', '11111111-1111-1111-1111-111111111111', 'Shorts Sport', 35.00, 5.00, 'Sporty shorts', '{"size":["S","M","L","XL"],"material":"polyester"}'),
    ('550e8400-e29b-41d4-a716-000000000013', '11111111-1111-1111-1111-111111111111', 'Cap Classic', 15.00, 3.00, 'Classic cap', '{"size":["S","M","L"],"material":"cotton"}'),
    ('550e8400-e29b-41d4-a716-000000000014', '11111111-1111-1111-1111-111111111111', 'Cap Sport', 20.00, 3.00, 'Sporty cap', '{"size":["S","M","L"],"material":"polyester"}'),
    ('550e8400-e29b-41d4-a716-000000000015', '11111111-1111-1111-1111-111111111111', 'Sneakers Casual', 80.00, 10.00, 'Casual sneakers', '{"size":["38","39","40","41","42"],"material":"canvas"}'),
    ('550e8400-e29b-41d4-a716-000000000016', '11111111-1111-1111-1111-111111111111', 'Sneakers Sport', 100.00, 10.00, 'Sport sneakers', '{"size":["38","39","40","41","42"],"material":"synthetic"}'),
    ('550e8400-e29b-41d4-a716-000000000017', '11111111-1111-1111-1111-111111111111', 'Socks Cotton', 10.00, 2.00, 'Cotton socks pack', '{"size":["M","L"],"material":"cotton"}'),
    ('550e8400-e29b-41d4-a716-000000000018', '11111111-1111-1111-1111-111111111111', 'Belt Leather', 25.00, 3.00, 'Leather belt', '{"size":["M","L","XL"],"material":"leather"}'),
    ('550e8400-e29b-41d4-a716-000000000019', '11111111-1111-1111-1111-111111111111', 'Scarf Winter', 20.00, 2.50, 'Warm wool scarf', '{"size":["OneSize"],"material":"wool"}'),
    ('550e8400-e29b-41d4-a716-000000000020', '11111111-1111-1111-1111-111111111111', 'Gloves Winter', 18.00, 2.50, 'Warm gloves', '{"size":["S","M","L"],"material":"wool"}');

-- Warianty (3 warianty dla każdego produktu, razem 60 wariantów)
INSERT INTO variants (id, product_id, price, stock_quantity, image_url, additional_properties, description, created_at, updated_at)
VALUES
-- T-Shirt Basic
('660e8400-e29b-41d4-a716-000000000001', '550e8400-e29b-41d4-a716-000000000001', 25.00, 50, 'https://example.com/images/tshirt-basic-white.jpg', '{"color":"white"}', 'White T-Shirt, size M', now(), now()),
('660e8400-e29b-41d4-a716-000000000002', '550e8400-e29b-41d4-a716-000000000001', 25.00, 30, 'https://example.com/images/tshirt-basic-black.jpg', '{"color":"black"}', 'Black T-Shirt, size M', now(), now()),
('660e8400-e29b-41d4-a716-000000000003', '550e8400-e29b-41d4-a716-000000000001', 25.00, 20, 'https://example.com/images/tshirt-basic-red.jpg', '{"color":"red"}', 'Red T-Shirt, size M', now(), now()),

-- T-Shirt Premium
('660e8400-e29b-41d4-a716-000000000004', '550e8400-e29b-41d4-a716-000000000002', 35.00, 40, 'https://example.com/images/tshirt-premium-white.jpg', '{"color":"white"}', 'White Premium T-Shirt', now(), now()),
('660e8400-e29b-41d4-a716-000000000005', '550e8400-e29b-41d4-a716-000000000002', 35.00, 35, 'https://example.com/images/tshirt-premium-black.jpg', '{"color":"black"}', 'Black Premium T-Shirt', now(), now()),
('660e8400-e29b-41d4-a716-000000000006', '550e8400-e29b-41d4-a716-000000000002', 35.00, 25, 'https://example.com/images/tshirt-premium-blue.jpg', '{"color":"blue"}', 'Blue Premium T-Shirt', now(), now()),

-- Jeans Slim Fit
('660e8400-e29b-41d4-a716-000000000007', '550e8400-e29b-41d4-a716-000000000003', 60.00, 40, 'https://example.com/images/jeans-slim-32.jpg', '{"size":"32"}', 'Slim Fit Jeans, size 32', now(), now()),
('660e8400-e29b-41d4-a716-000000000008', '550e8400-e29b-41d4-a716-000000000003', 60.00, 35, 'https://example.com/images/jeans-slim-34.jpg', '{"size":"34"}', 'Slim Fit Jeans, size 34', now(), now()),
('660e8400-e29b-41d4-a716-000000000009', '550e8400-e29b-41d4-a716-000000000003', 60.00, 30, 'https://example.com/images/jeans-slim-36.jpg', '{"size":"36"}', 'Slim Fit Jeans, size 36', now(), now()),

-- Jeans Regular Fit
('660e8400-e29b-41d4-a716-000000000010', '550e8400-e29b-41d4-a716-000000000004', 55.00, 45, 'https://example.com/images/jeans-regular-32.jpg', '{"size":"32"}', 'Regular Fit Jeans, size 32', now(), now()),
('660e8400-e29b-41d4-a716-000000000011', '550e8400-e29b-41d4-a716-000000000004', 55.00, 40, 'https://example.com/images/jeans-regular-34.jpg', '{"size":"34"}', 'Regular Fit Jeans, size 34', now(), now()),
('660e8400-e29b-41d4-a716-000000000012', '550e8400-e29b-41d4-a716-000000000004', 55.00, 35, 'https://example.com/images/jeans-regular-36.jpg', '{"size":"36"}', 'Regular Fit Jeans, size 36', now(), now()),

-- Hoodie Winter
('660e8400-e29b-41d4-a716-000000000013', '550e8400-e29b-41d4-a716-000000000005', 80.00, 25, 'https://example.com/images/hoodie-winter-gray.jpg', '{"color":"gray"}', 'Gray Hoodie Winter, size L', now(), now()),
('660e8400-e29b-41d4-a716-000000000014', '550e8400-e29b-41d4-a716-000000000005', 85.00, 15, 'https://example.com/images/hoodie-winter-black.jpg', '{"color":"black"}', 'Black Hoodie Winter, size XL', now(), now()),
('660e8400-e29b-41d4-a716-000000000015', '550e8400-e29b-41d4-a716-000000000005', 82.00, 20, 'https://example.com/images/hoodie-winter-blue.jpg', '{"color":"blue"}', 'Blue Hoodie Winter, size M', now(), now());

-- Analogicznie dodaj warianty dla pozostałych produktów do 20
-- W sumie dla 20 produktów po 3 warianty = 60 wierszy
