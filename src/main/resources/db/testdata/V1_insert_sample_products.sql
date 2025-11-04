INSERT INTO categories(id, parent_category_id, name)
VALUES
    ('ebee37f7-39db-44de-a9d5-cec0db9f2be8', NULL, 'clothes'),
    ('723763f3-9c3f-4ff7-af63-a8d320d2ba5a', NULL, 'electronics'),
    ('0d823ef4-d71b-4a6e-93dc-430cb76cbfd6', NULL, 'books'),
    ('4ca34634-553e-491f-9f2f-b891bdeb4e34', NULL, 'beauty'),
    ('af59dcc7-209b-4823-a41f-d1cd5636ec1b', NULL, 'sports'),
    ('871ee368-bb92-4fe8-8d61-c86eada64fe7', NULL, 'toys'),
    ('03a2de6c-8bc5-437a-98c3-4133d61ebebb', NULL, 'food');

INSERT INTO products(id, category_id, name, approximate_price, delivery_price, description, info)
VALUES
    ('82e3eecf-69e7-4465-878a-0a8b40a5f938', 'ebee37f7-39db-44de-a9d5-cec0db9f2be8', 'The North Face QUEST - Kurtka przeciwdeszczowa', 399.0, 15.0, 'Dyscyplina sportowa. Piesze wędrówki i trekking, Alpinizm. Oddychające, wiatroszczelny, wodoszczelny. Polartec', NULL),
    ('24dcd5af-5230-4882-ba60-581ba42ab0d0', 'ebee37f7-39db-44de-a9d5-cec0db9f2be8', 'Mammut - Kurtka zimowa', 918.0, 15.0, 'Dla ciepła i komfortu w warunkach poniżej zera, ta kurtka termiczna jest izolowana specjalistycznym wypełnieniem syntetycznym: naszym wyjątkowym Mammut LOOPINSULATION, wykonanym z recyklingowanych skrawków lin. W połączeniu z recyklingowanym, wiatroszczelnym materiałem zewnętrznym, ta przytulna warstwa zapewnia potrzebną ochronę, jednocześnie stanowiąc ważny krok w kierunku bardziej cyrkularnej gospodarki i odpowiedzialnej produkcji o możliwie najmniejszym wpływie na środowisko.', NULL),
    ('8f354eb9-5bb3-4bec-ab00-dac003695f6d', 'ebee37f7-39db-44de-a9d5-cec0db9f2be8', 'U.S.Polo Assn. ARJUN - T-shirt basic', 82.99, 15.0, 'Material: 100% bawelna', NULL);

INSERT INTO variants(id, product_id, price, stock_quantity, image_url, additional_properties, description, created_at, updated_at)
VALUES
    ('658a874b-c8cd-40ee-9cf6-6a801b8eb69a', '82e3eecf-69e7-4465-878a-0a8b40a5f938', 399.0, 50, 'https://img01.ztat.net/article/spp-media-p1/e7d5535687874a518997522421231596/083a79b5507d44b0ba37460bf7169961.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('7b0d4bf6-497f-43c7-b82d-3e89b8b63b57', '82e3eecf-69e7-4465-878a-0a8b40a5f938', 399.0, 11, 'https://img01.ztat.net/article/spp-media-p1/ebd3ee30fb1c4fcf925c53b309284aaa/74ccfa030ff749038bb95be23ebd3289.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('45056300-253b-4d4d-9ff8-411d40e7d8cf', '82e3eecf-69e7-4465-878a-0a8b40a5f938', 399.0, 33, 'https://img01.ztat.net/article/spp-media-p1/0191884300c544d48b3687e4b2d7f81d/ca57701fb7a84baa9c5ad82bce0dd4ed.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('ba8c0799-f5e6-4174-bb20-21041c51db89', '82e3eecf-69e7-4465-878a-0a8b40a5f938', 399.0, 28, 'https://img01.ztat.net/article/spp-media-p1/68cd85274d6048b8a8c0b62ecf375583/58d90715482048ecbc6491c97d6807d4.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('d6b17628-71b4-4b7c-8f25-84e4cf954ced', '82e3eecf-69e7-4465-878a-0a8b40a5f938', 399.0, 16, 'https://img01.ztat.net/article/spp-media-p1/bc6dbbf7f7674b9c8a39aa67f5ba8cfb/44db2655e7f24e81b5de7df334a2f8ea.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('c875324a-d1cb-44e9-8c55-a24f37e26b95', '24dcd5af-5230-4882-ba60-581ba42ab0d0', 918.0, 15, 'https://img01.ztat.net/article/spp-media-p1/ba4f0ad953af4463bf4d4b36b35d4bdb/f4c9066334ab44f2a319ec91a9ff746d.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('bb1f8da5-97c7-4973-976e-e6f16f5a77a4', '24dcd5af-5230-4882-ba60-581ba42ab0d0', 918.0, 43, 'https://img01.ztat.net/article/spp-media-p1/4b68693a8f504a5b926f1cb1fce506c4/03821976caa94069b6a1b28ab91373d8.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('d04763e9-d0ba-415f-bdee-ba2c89b63001', '24dcd5af-5230-4882-ba60-581ba42ab0d0', 918.0, 47, 'https://img01.ztat.net/article/spp-media-p1/ba349ba1932b49cbb7157ec899b362de/6c1d625f149f4cf582c2243ed2bf223d.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('dbde8243-2854-47c9-804d-41bcb11e684c', '24dcd5af-5230-4882-ba60-581ba42ab0d0', 918.0, 39, 'https://img01.ztat.net/article/spp-media-p1/9f4a39b09e0c48b7baa9c05ccd26253d/d51c5d2c6f024affa35a34fa1aed25f5.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('31f01517-b1eb-44a5-8552-8172e789aaf9', '8f354eb9-5bb3-4bec-ab00-dac003695f6d', 82.99, 39, 'https://img01.ztat.net/article/spp-media-p1/0088b3625880488a8f3ff2e935b6473c/445d0f4e58de4268bb8bdaa39edf07e1.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('71819159-c1b0-4060-8338-bb0bafb2f1d3', '8f354eb9-5bb3-4bec-ab00-dac003695f6d', 82.99, 41, 'https://img01.ztat.net/article/spp-media-p1/433f1541d31a4ec18c689a1b9a8e3dd7/e429555182eb496b91c871e10a90e4e5.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272'),
    ('7c7cb830-66ed-47ee-aa2a-75c73142d354', '8f354eb9-5bb3-4bec-ab00-dac003695f6d', 82.99, 14, 'https://img01.ztat.net/article/spp-media-p1/d333af070425452688ce2fc6434b3227/70619155a40542999decc319c2a91221.jpg?imwidth=1800', NULL, '', '2025-11-04 23:29:39.696272', '2025-11-04 23:29:39.696272');

