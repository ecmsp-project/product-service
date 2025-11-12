-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2025-09-23 18:49:25.885

-- tables
-- Table: categories
CREATE TABLE categories (
                            id uuid  NOT NULL,
                            name varchar(255)  NOT NULL,
                            parent_category_id uuid  NULL,
                            CONSTRAINT categories_pk PRIMARY KEY (id)
);

-- Table: default_property_options
CREATE TABLE default_property_options (
                                          id uuid  NOT NULL,
                                          property_id uuid  NOT NULL,
                                          value_text text  NULL,
                                          value_decimal decimal(10,2)  NULL,
                                          value_boolean boolean  NULL,
                                          value_date date  NULL,
                                          display_text text  NOT NULL,
                                          CONSTRAINT default_property_options_pk PRIMARY KEY (id)
);

-- Table: products
CREATE TABLE products (
                          id uuid  NOT NULL,
                          category_id uuid  NOT NULL,
                          name varchar(255)  NOT NULL,
                          approximate_price decimal(12,2)  NOT NULL,
                          delivery_price decimal(12,2)  NOT NULL,
                          description text  NULL,
                          info jsonb  NULL,
                          CONSTRAINT products_pk PRIMARY KEY (id)
);

-- Table: properties
CREATE TABLE properties (
                            id uuid  NOT NULL,
                            category_id uuid  NOT NULL,
                            name varchar(255)  NOT NULL,
                            unit varchar(50)  NULL,
                            data_type text  NOT NULL CHECK (data_type IN ('TEXT', 'NUMBER', 'BOOLEAN', 'DATE')),
                            required boolean  NOT NULL,
                            has_default_options boolean  NOT NULL,
                            CONSTRAINT properties_pk PRIMARY KEY (id)
);

-- Table: variant_properties
CREATE TABLE variant_properties (
                                    id uuid  NOT NULL,
                                    variant_id uuid  NOT NULL,
                                    property_id uuid  NOT NULL,
                                    value_text text  NULL,
                                    value_decimal decimal(10,2)  NULL,
                                    value_boolean boolean  NULL,
                                    value_date date  NULL,
                                    display_text text  NULL,
                                    CONSTRAINT variant_properties_pk PRIMARY KEY (id)
);

-- Table: variant_reservations
CREATE TABLE variant_reservations (
                                      id uuid  NOT NULL,
                                      reservation_id uuid NOT NULL,
                                      variant_id uuid  NOT NULL,
                                      reserved_quantity int  NOT NULL,
                                      created_at timestamp  NOT NULL,
                                      expires_at timestamp  NOT NULL,
                                      status text  NOT NULL CHECK (status IN ('ACTIVE','CANCELLED','EXPIRED', 'PAYMENT_COMPLETED', 'PAYMENT_FAILED')),
                                      CONSTRAINT variant_reservations_pk PRIMARY KEY (id)
);

-- Table: variants
CREATE TABLE variants (
                          id uuid  NOT NULL,
                          product_id uuid  NOT NULL,
                          price decimal(12,2)  NOT NULL,
                          stock_quantity int  NOT NULL,
                          image_url varchar(255)  NOT NULL,
                          additional_properties jsonb  NULL,
                          description text  NULL,
                          created_at timestamp  NOT NULL,
                          updated_at timestamp  NOT NULL,
                          CONSTRAINT variants_pk PRIMARY KEY (id)
);

-- foreign keys
-- Reference: Copy_of_product_category_product (table: products)
ALTER TABLE products ADD CONSTRAINT Copy_of_product_category_product
    FOREIGN KEY (category_id)
        REFERENCES categories (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: Copy_of_product_category_product_category (table: categories)
ALTER TABLE categories ADD CONSTRAINT Copy_of_product_category_product_category
    FOREIGN KEY (parent_category_id)
        REFERENCES categories (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: attribute_category (table: properties)
ALTER TABLE properties ADD CONSTRAINT attribute_category
    FOREIGN KEY (category_id)
        REFERENCES categories (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: attribute_values_attributes (table: default_property_options)
ALTER TABLE default_property_options ADD CONSTRAINT attribute_values_attributes
    FOREIGN KEY (property_id)
        REFERENCES properties (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: variant_attribute_attribute (table: variant_properties)
ALTER TABLE variant_properties ADD CONSTRAINT variant_attribute_attribute
    FOREIGN KEY (property_id)
        REFERENCES properties (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: variant_attribute_variant (table: variant_properties)
ALTER TABLE variant_properties ADD CONSTRAINT variant_attribute_variant
    FOREIGN KEY (variant_id)
        REFERENCES variants (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: variant_product (table: variants)
ALTER TABLE variants ADD CONSTRAINT variant_product
    FOREIGN KEY (product_id)
        REFERENCES products (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: variant_reservations_variants (table: variant_reservations)
ALTER TABLE variant_reservations ADD CONSTRAINT variant_reservations_variants
    FOREIGN KEY (variant_id)
        REFERENCES variants (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

INSERT INTO categories(id, parent_category_id, name)
VALUES
    ('436bd2e4-a4d7-43dd-b6ef-324051662b33', NULL, 'Clothes'),
    ('08864c09-de3f-4961-b27a-261ba4b86215', NULL, 'Electronics'),
    ('9a4ef95d-f0a6-4378-9ba4-dc61aa5607f0', NULL, 'Books'),
    ('340343ae-dfe0-460c-85c1-75cf45a0781e', NULL, 'Beauty'),
    ('cc380e52-4702-41b2-9759-65ad9d02719e', NULL, 'Sports'),
    ('dbe307d6-e222-477b-ab5d-e7aeb3511b22', NULL, 'Toys'),
    ('c0ce23fe-fcbb-4170-9f5c-cd5702744679', NULL, 'Food');

INSERT INTO products(id, category_id, name, approximate_price, delivery_price, description, info)
VALUES
    ('cf5fb032-508b-4f3f-b547-5a37674bb423', '436bd2e4-a4d7-43dd-b6ef-324051662b33', 'The North Face QUEST - Kurtka przeciwdeszczowa', 399.0, 15.0, 'Dyscyplina sportowa. Piesze wędrówki i trekking, Alpinizm. Oddychające, wiatroszczelny, wodoszczelny. Polartec', NULL),
    ('fec8eb91-d795-4485-b833-acc1b494ab51', '436bd2e4-a4d7-43dd-b6ef-324051662b33', 'Mammut - Kurtka zimowa', 918.0, 15.0, 'Dla ciepła i komfortu w warunkach poniżej zera, ta kurtka termiczna jest izolowana specjalistycznym wypełnieniem syntetycznym: naszym wyjątkowym Mammut LOOPINSULATION, wykonanym z recyklingowanych skrawków lin. W połączeniu z recyklingowanym, wiatroszczelnym materiałem zewnętrznym, ta przytulna warstwa zapewnia potrzebną ochronę, jednocześnie stanowiąc ważny krok w kierunku bardziej cyrkularnej gospodarki i odpowiedzialnej produkcji o możliwie najmniejszym wpływie na środowisko.', NULL),
    ('dbd4428d-ebbe-47ad-96de-2bea38148513', '436bd2e4-a4d7-43dd-b6ef-324051662b33', 'U.S.Polo Assn. ARJUN - T-shirt basic', 82.99, 15.0, 'Material: 100% bawelna', NULL);

INSERT INTO variants(id, product_id, price, stock_quantity, image_url, additional_properties, description, created_at, updated_at)
VALUES
    ('6ed45142-524c-4248-bce8-5e605e4effae', 'cf5fb032-508b-4f3f-b547-5a37674bb423', 399.0, 30, 'https://img01.ztat.net/article/spp-media-p1/e7d5535687874a518997522421231596/083a79b5507d44b0ba37460bf7169961.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('76285c8f-7665-4b95-8f9f-00c02a6e6211', 'cf5fb032-508b-4f3f-b547-5a37674bb423', 399.0, 1, 'https://img01.ztat.net/article/spp-media-p1/ebd3ee30fb1c4fcf925c53b309284aaa/74ccfa030ff749038bb95be23ebd3289.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('1165bba2-22e3-4181-9d1e-1b4d752ddc17', 'cf5fb032-508b-4f3f-b547-5a37674bb423', 399.0, 42, 'https://img01.ztat.net/article/spp-media-p1/0191884300c544d48b3687e4b2d7f81d/ca57701fb7a84baa9c5ad82bce0dd4ed.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('7ebb56d9-3d0b-4008-a112-b76bdf819e38', 'cf5fb032-508b-4f3f-b547-5a37674bb423', 399.0, 3, 'https://img01.ztat.net/article/spp-media-p1/68cd85274d6048b8a8c0b62ecf375583/58d90715482048ecbc6491c97d6807d4.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('0824149d-d0ca-49df-8f98-b534374c11fb', 'cf5fb032-508b-4f3f-b547-5a37674bb423', 399.0, 38, 'https://img01.ztat.net/article/spp-media-p1/bc6dbbf7f7674b9c8a39aa67f5ba8cfb/44db2655e7f24e81b5de7df334a2f8ea.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('7c9c92a7-ce84-4808-8730-71b7a542751a', 'fec8eb91-d795-4485-b833-acc1b494ab51', 918.0, 15, 'https://img01.ztat.net/article/spp-media-p1/ba4f0ad953af4463bf4d4b36b35d4bdb/f4c9066334ab44f2a319ec91a9ff746d.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('b7682bcb-95f1-4efb-b647-61b849d9a383', 'fec8eb91-d795-4485-b833-acc1b494ab51', 918.0, 37, 'https://img01.ztat.net/article/spp-media-p1/4b68693a8f504a5b926f1cb1fce506c4/03821976caa94069b6a1b28ab91373d8.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('5c22cbd5-cbbe-4874-813e-df9f7ecc8d28', 'fec8eb91-d795-4485-b833-acc1b494ab51', 918.0, 16, 'https://img01.ztat.net/article/spp-media-p1/ba349ba1932b49cbb7157ec899b362de/6c1d625f149f4cf582c2243ed2bf223d.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('3e655a42-d07e-4e65-9b56-bbc279fe8384', 'fec8eb91-d795-4485-b833-acc1b494ab51', 918.0, 28, 'https://img01.ztat.net/article/spp-media-p1/9f4a39b09e0c48b7baa9c05ccd26253d/d51c5d2c6f024affa35a34fa1aed25f5.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('bce4939f-cc54-4dd2-97fe-ab2237ff1a6c', 'dbd4428d-ebbe-47ad-96de-2bea38148513', 82.99, 37, 'https://img01.ztat.net/article/spp-media-p1/0088b3625880488a8f3ff2e935b6473c/445d0f4e58de4268bb8bdaa39edf07e1.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('0b93bd82-d438-4216-b9cf-02bf97f68fcf', 'dbd4428d-ebbe-47ad-96de-2bea38148513', 82.99, 46, 'https://img01.ztat.net/article/spp-media-p1/433f1541d31a4ec18c689a1b9a8e3dd7/e429555182eb496b91c871e10a90e4e5.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712'),
    ('3c733052-0c4a-441a-ae03-8db0bba54363', 'dbd4428d-ebbe-47ad-96de-2bea38148513', 82.99, 6, 'https://img01.ztat.net/article/spp-media-p1/d333af070425452688ce2fc6434b3227/70619155a40542999decc319c2a91221.jpg?imwidth=1800', NULL, '', '2025-11-11 21:28:07.328712', '2025-11-11 21:28:07.328712');

INSERT INTO categories(id, parent_category_id, name)
VALUES
    ('44d5ba6b-6040-404e-b615-2348e0d7eb4f', '436bd2e4-a4d7-43dd-b6ef-324051662b33', 'Man'),
    ('56f5bdcd-8f45-41ba-8f41-795a363bb6e7', '436bd2e4-a4d7-43dd-b6ef-324051662b33', 'Woman'),
    ('4b503b60-0e89-4cd7-a5c2-5acee887fa2e', '08864c09-de3f-4961-b27a-261ba4b86215', 'Computers'),
    ('90b1ab8b-a317-492c-a8e4-b5c736446729', '08864c09-de3f-4961-b27a-261ba4b86215', 'Mobile Phones'),
    ('1285b058-fc31-442b-9172-e4d20703b0ae', '08864c09-de3f-4961-b27a-261ba4b86215', 'Audio'),
    ('4e3e75db-871f-4ae9-8525-b52985be08d7', '08864c09-de3f-4961-b27a-261ba4b86215', 'TV'),
    ('98c159ea-cc4d-4799-b562-ec2f516836ed', '08864c09-de3f-4961-b27a-261ba4b86215', 'Smart Home'),
    ('c1ff73bf-7b4a-43b8-a006-91075bbfbfa1', '08864c09-de3f-4961-b27a-261ba4b86215', 'Drones'),
    ('730ceb21-5271-4a57-b90f-11310dce84c7', '08864c09-de3f-4961-b27a-261ba4b86215', 'Networking & Wi-Fi Devices'),
    ('5584bf07-33e5-406c-b662-f58307545086', '340343ae-dfe0-460c-85c1-75cf45a0781e', 'Skincare'),
    ('35e948bb-7938-42ea-b6ea-c5d892e1999f', '340343ae-dfe0-460c-85c1-75cf45a0781e', 'Makeup'),
    ('43f7f577-1018-45c0-bdab-16b24b9d2286', '340343ae-dfe0-460c-85c1-75cf45a0781e', 'Haircare'),
    ('623c5fa7-79c2-438f-8d6e-d672e13dc32f', '340343ae-dfe0-460c-85c1-75cf45a0781e', 'Fragrance'),
    ('c0a526ce-b626-4edc-a1aa-c9e07cb8027c', '340343ae-dfe0-460c-85c1-75cf45a0781e', 'Nail Care'),
    ('efb086c2-f769-44bb-b626-ac1994114cce', '340343ae-dfe0-460c-85c1-75cf45a0781e', 'Bath & Body'),
    ('54c612a9-0ac3-4fcf-80f2-480f8547b19f', 'cc380e52-4702-41b2-9759-65ad9d02719e', 'Team Sports'),
    ('da1b628d-2354-4d2b-9770-7cff279db969', 'cc380e52-4702-41b2-9759-65ad9d02719e', 'Individual Sports'),
    ('452655e1-39a7-4e04-a751-8cb5ce550e97', 'cc380e52-4702-41b2-9759-65ad9d02719e', 'Fitness & Exercise'),
    ('69442bd2-0a8f-4cf3-a8a2-bcb1f44779f9', 'cc380e52-4702-41b2-9759-65ad9d02719e', 'Outdoor & Adventure'),
    ('2175265c-192f-4a98-95ff-3568211c3121', 'dbe307d6-e222-477b-ab5d-e7aeb3511b22', 'Educational Toys'),
    ('668f2452-2db8-4fbf-acb1-42d2f97d8361', 'dbe307d6-e222-477b-ab5d-e7aeb3511b22', 'Electronics & Robotic Toys'),
    ('7c3d538f-484b-40ef-818a-017baeb4049c', 'dbe307d6-e222-477b-ab5d-e7aeb3511b22', 'Game & Puzzles'),
    ('f263cea9-1b51-4023-b02f-16ad8ba925e1', 'dbe307d6-e222-477b-ab5d-e7aeb3511b22', 'Building & Construction Toys');

