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

-- Table: variant_images
CREATE TABLE variant_images (
                                id uuid  NOT NULL,
                                variant_id uuid  NOT NULL,
                                url TEXT  NOT NULL,
                                is_main boolean  DEFAULT FALSE NOT NULL,
                                position int DEFAULT 1 NOT NULL,
                                created_at timestamp  NOT NULL,
                                updated_at timestamp  NOT NULL,
                                CONSTRAINT variant_images_pk PRIMARY KEY (id)
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

-- Reference: variant_images_variants (table: variant_images)
ALTER TABLE variant_images ADD CONSTRAINT variant_images_variants
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
    ('bf3ab474-42a4-426a-a867-d2bf2d2604c9', NULL, 'Clothes'),
    ('36fae0e5-b2d8-4e4b-8730-67b43212571a', NULL, 'Electronics'),
    ('4aadca3e-0e1d-4aa3-bc94-b75b9e2100cb', NULL, 'Books'),
    ('48a7deca-a15b-4949-9c8d-edc7916f7f58', NULL, 'Beauty'),
    ('46e4272d-7b2a-4311-845b-78a3bd8edc39', NULL, 'Sports'),
    ('e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', NULL, 'Toys'),
    ('21170671-dd8a-4c25-8fbb-87be611f75a4', NULL, 'Food');

INSERT INTO categories(id, parent_category_id, name)
VALUES
    ('9b8a7e81-df2e-4f1b-a67a-30d853b64c32', 'bf3ab474-42a4-426a-a867-d2bf2d2604c9', 'Man'),
    ('5908a3ea-dc6b-431c-a379-c1f399be8d4c', 'bf3ab474-42a4-426a-a867-d2bf2d2604c9', 'Woman'),
    ('cf7683eb-403c-4ad1-a0a8-60db35668ae9', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Computers'),
    ('5e9f0f94-12b7-410d-821f-a944e0024720', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Mobile Phones'),
    ('9ad87f89-9440-44dc-97a6-e7657cf6f575', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Audio'),
    ('3f585eec-6f76-4786-b8e2-fbdcab1fbe80', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'TV'),
    ('005f22e0-2eeb-4ea7-a221-35b10f70a0f2', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Smart Home'),
    ('48a9ed3c-5fb0-457c-8125-963db5a8db56', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Drones'),
    ('074b709c-9e70-4315-be70-b60cf84f30c0', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Networking & Wi-Fi Devices'),
    ('4450f782-f85e-43f9-997d-f8ede9fdecf4', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Laptops'),
    ('42ebfc1c-9c98-4b10-a7dc-df754f175a56', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Skincare'),
    ('7d008420-732c-46bb-9483-d938e4cb6f38', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Makeup'),
    ('844b5165-67d3-4ffa-bc2e-ff8a727c0d95', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Haircare'),
    ('d16abf67-b7de-4c2c-80b6-da93802ce56b', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Fragrance'),
    ('03f8b1d2-f9ee-4ea4-b865-2b99955f1d43', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Nail Care'),
    ('bde1da04-2275-432c-bf20-a6306b2d367e', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Bath & Body'),
    ('f063fdee-b8c9-4ffb-bef8-78ccff6276ac', '46e4272d-7b2a-4311-845b-78a3bd8edc39', 'Team Sports'),
    ('8e8ce81d-10b5-448c-880c-54fc8f47f4cd', '46e4272d-7b2a-4311-845b-78a3bd8edc39', 'Individual Sports'),
    ('e76cd650-4e2c-4635-b000-a71f8ee4d2a5', '46e4272d-7b2a-4311-845b-78a3bd8edc39', 'Fitness & Exercise'),
    ('477845f9-0717-470a-bfef-7fb313426ced', '46e4272d-7b2a-4311-845b-78a3bd8edc39', 'Outdoor & Adventure'),
    ('dcb0564d-fa42-4c49-af45-2b378a1f2b8e', 'e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', 'Educational Toys'),
    ('49b31f0f-fcd7-4bcf-83ce-fd721f995e9e', 'e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', 'Electronics & Robotic Toys'),
    ('c45eb190-001f-4d25-ae4f-639da3bfa54e', 'e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', 'Game & Puzzles'),
    ('f7b54422-41eb-427d-915e-28abe5d7ea89', 'e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', 'Building & Construction Toys');

INSERT INTO products(id, category_id, name, approximate_price, delivery_price, description, info)
VALUES
    ('d28e05cf-babb-4548-b7dc-d9b3f6fad304', 'bf3ab474-42a4-426a-a867-d2bf2d2604c9', 'The North Face QUEST - Kurtka przeciwdeszczowa', 399.0, 15.0, 'Dyscyplina sportowa. Piesze wędrówki i trekking, Alpinizm. Oddychające, wiatroszczelny, wodoszczelny. Polartec', NULL),
    ('80e8b384-67bc-4dd3-b76e-70e52e05a471', '5e9f0f94-12b7-410d-821f-a944e0024720', 'Xiaomi Redmi Note 14 6+128GB 6.67" 4G Midnight Black NFC EU', 529.99, 15.0, 'Product Information 6.67" AMOLED 120Hz Display: Smooth visuals and vibrant colors with peak brightness of 1800 nits. An immersive viewing experience. 108MP Rear Camera + 20MP Selfie Camera: Capture every detail with precision thanks to a professional aluminum camera system. MediaTek G99-Ultra Processor: Optimal power for gaming and multitasking without compromise, no NFC. 5500mAh Battery + 33W Fast Charging: Long-lasting battery life and ultra-fast charging to keep you connected. Premium Security and Sound: Built-in fingerprint sensor and dual Dolby speakers for a complete experience.', NULL),
    ('c9c17b28-26f6-4746-a0d7-41d80d66dd2e', '4450f782-f85e-43f9-997d-f8ede9fdecf4', 'APPLE MacBook Air 13'' M4', 4199.0, 15.0, 'Speed. Lightness. Freedom – The MacBook Air with the M4 chip excels at work and play. With up to 18 hours* of battery life and an incredibly portable design, you can tackle any task anywhere. Turbocharged with the M4 Chip – The Apple M4 chip delivers even greater speed and smoothness in everything you do, from multitasking and video editing to playing graphically demanding games. Up to 18 Hours of Battery Life – The MacBook Air is incredibly efficient whether on battery power or plugged in*. Stunning Display – The 13.6-inch Liquid Retina display supports a billion colors*. Photos and videos impress with contrast and detail, while text is exceptionally sharp. Looks and Sounds Amazing – Thanks to the 12MP Center Stage camera, three microphones, and four speakers with spatial audio, everything looks and sounds fantastic. Plenty of Connections – The MacBook Air features two Thunderbolt 4 ports, a MagSafe charging port, a headphone jack, and Wi‑Fi 6E* and Bluetooth 5.3. You can even connect up to two external displays. Apps Fly in macOS – Your favorite apps, including Microsoft 365 Copilot, Adobe Creative Cloud, and Google Workspace, run faster than ever in macOS*. iPhone Lovers Will Love Mac – Mac works seamlessly with all Apple devices. Copy something on your iPhone and paste it on your Mac. Send a message from Mac or answer a FaceTime call*.', NULL);

INSERT INTO variants(id, product_id, price, stock_quantity, image_url, additional_properties, description, created_at, updated_at)
VALUES
    ('f0d39eb0-91d8-400b-89c4-ee16724ea310', 'd28e05cf-babb-4548-b7dc-d9b3f6fad304', 399.0, 50, 'https://img01.ztat.net/article/spp-media-p1/e7d5535687874a518997522421231596/083a79b5507d44b0ba37460bf7169961.jpg?imwidth=1800', NULL, '', '2025-11-13 20:53:22.453130', '2025-11-13 20:53:22.453130'),
    ('b275e98c-6afc-48c4-9bf2-0cd65d4686f4', 'd28e05cf-babb-4548-b7dc-d9b3f6fad304', 399.0, 26, 'https://img01.ztat.net/article/spp-media-p1/ebd3ee30fb1c4fcf925c53b309284aaa/74ccfa030ff749038bb95be23ebd3289.jpg?imwidth=1800', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130'),
    ('7e383b81-de90-46c3-b152-e099b1b0b3fa', 'd28e05cf-babb-4548-b7dc-d9b3f6fad304', 399.0, 38, 'https://img01.ztat.net/article/spp-media-p1/0191884300c544d48b3687e4b2d7f81d/ca57701fb7a84baa9c5ad82bce0dd4ed.jpg?imwidth=1800', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130'),
    ('d9c617c0-645a-428b-9087-c06b903ba9ee', 'd28e05cf-babb-4548-b7dc-d9b3f6fad304', 399.0, 27, 'https://img01.ztat.net/article/spp-media-p1/68cd85274d6048b8a8c0b62ecf375583/58d90715482048ecbc6491c97d6807d4.jpg?imwidth=1800', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130'),
    ('67e9c8ef-8df6-49ab-ba09-3cc0f9bf3f31', 'd28e05cf-babb-4548-b7dc-d9b3f6fad304', 399.0, 45, 'https://img01.ztat.net/article/spp-media-p1/bc6dbbf7f7674b9c8a39aa67f5ba8cfb/44db2655e7f24e81b5de7df334a2f8ea.jpg?imwidth=1800', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130'),
    ('4d49fb76-2d75-4337-ac65-d05a6f8eabdb', '80e8b384-67bc-4dd3-b76e-70e52e05a471', 439.99, 34, 'https://m.media-amazon.com/images/I/81JrYIgLUlL._AC_SL1500_.jpg', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130'),
    ('88038014-2c91-47f6-a90a-74adf663eb97', '80e8b384-67bc-4dd3-b76e-70e52e05a471', 619.99, 24, 'https://m.media-amazon.com/images/I/81JrYIgLUlL._AC_SL1500_.jpg', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130'),
    ('cb342ff9-fcc3-4c24-9845-b43f23856f7d', 'c9c17b28-26f6-4746-a0d7-41d80d66dd2e', 3799.0, 24, 'https://m.media-amazon.com/images/I/71ZdDLeldzL._AC_SL1500_.jpg', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130'),
    ('a04eb31b-cc1d-4cc4-a582-c362e32847a1', 'c9c17b28-26f6-4746-a0d7-41d80d66dd2e', 3899.0, 13, 'https://m.media-amazon.com/images/I/71ZdDLeldzL._AC_SL1500_.jpg', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130'),
    ('e64a582e-16cd-42e2-9f3b-e0ff017a812f', 'c9c17b28-26f6-4746-a0d7-41d80d66dd2e', 4599.0, 24, 'https://m.media-amazon.com/images/I/71ZdDLeldzL._AC_SL1500_.jpg', NULL, '', '2025-11-13 20:53:22.454130', '2025-11-13 20:53:22.454130');

INSERT INTO categories(id, parent_category_id, name)
VALUES
    ('dc2f467f-f25e-4da2-a8fa-91b53045cc99', 'bf3ab474-42a4-426a-a867-d2bf2d2604c9', 'Man'),
    ('6d652d89-ebee-4319-a179-8e5d3f03e987', 'bf3ab474-42a4-426a-a867-d2bf2d2604c9', 'Woman'),
    ('b09a5a74-6373-4507-8a5e-4e4209ef8c52', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Computers'),
    ('b32cc5db-6d73-49ad-b076-574e741ec35c', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Mobile Phones'),
    ('28175c76-84f6-4930-be42-ddef9d900bab', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Audio'),
    ('efcb2f57-b47b-44c3-8064-e12e72f0b915', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'TV'),
    ('3328b638-8f4b-43de-acb2-269f808f9367', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Smart Home'),
    ('e6ffac43-1d3c-441d-9eed-8b90a62ef4fc', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Drones'),
    ('aa8c12eb-5b15-4320-a9df-05ab5337d269', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Networking & Wi-Fi Devices'),
    ('4987ca98-1b7b-4f17-a572-202b275b6e5e', '36fae0e5-b2d8-4e4b-8730-67b43212571a', 'Laptops'),
    ('21eb605b-6444-41a4-aca4-5c5e417efcf5', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Skincare'),
    ('86469b42-0ac9-4e9b-b8d4-acb225ab3af5', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Makeup'),
    ('fbe69dd3-0da5-46a0-ad18-659e1e7151fb', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Haircare'),
    ('836bbb32-400d-4cac-b1a3-693864ba5966', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Fragrance'),
    ('849ddf66-7db1-45a0-8e8b-f698db0db0f9', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Nail Care'),
    ('17e55e5f-4b38-44df-b3ed-fb63481fc1ec', '48a7deca-a15b-4949-9c8d-edc7916f7f58', 'Bath & Body'),
    ('5032018a-3ddb-42fb-9326-04dc47f74fd2', '46e4272d-7b2a-4311-845b-78a3bd8edc39', 'Team Sports'),
    ('f3557ec3-c76c-4ebd-b491-d892264e65fb', '46e4272d-7b2a-4311-845b-78a3bd8edc39', 'Individual Sports'),
    ('06938592-6029-4be1-ba96-cd0feca32ece', '46e4272d-7b2a-4311-845b-78a3bd8edc39', 'Fitness & Exercise'),
    ('9f3ccbf2-a7cc-4f04-83b1-19074bfc2768', '46e4272d-7b2a-4311-845b-78a3bd8edc39', 'Outdoor & Adventure'),
    ('58ef7c44-5d7f-4679-8fd7-4b3b3d80eaee', 'e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', 'Educational Toys'),
    ('18ef405a-c6c9-4ccb-aff7-1463fb698da2', 'e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', 'Electronics & Robotic Toys'),
    ('5d1711c7-db57-4848-840c-bdad1686e756', 'e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', 'Game & Puzzles'),
    ('04c1fdf5-55db-493a-bcb7-367c6a5c4606', 'e3a09df0-b23e-46a3-9c52-8b0257fa8f0f', 'Building & Construction Toys');

INSERT INTO variant_images(id, variant_id, url, is_main, position, created_at, updated_at)
VALUES
    ('0057c7fb-e59e-4d50-b9a1-25ed8f8ff04e', '4d49fb76-2d75-4337-ac65-d05a6f8eabdb', 'https://m.media-amazon.com/images/I/81JrYIgLUlL._AC_SL1500_.jpg', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('dc601447-2e7d-48f8-ad1a-590daf2215fc', '4d49fb76-2d75-4337-ac65-d05a6f8eabdb', 'https://m.media-amazon.com/images/I/71Z2W+g46ML._AC_SL1500_.jpg', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('b6e11a06-f80f-4a3b-b990-fe86321c27c3', '4d49fb76-2d75-4337-ac65-d05a6f8eabdb', 'https://m.media-amazon.com/images/I/615sM+fd2RL._AC_SL1500_.jpg', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('905d6f16-d4d8-4ead-80f9-1f61f4e5eb9b', '4d49fb76-2d75-4337-ac65-d05a6f8eabdb', 'https://m.media-amazon.com/images/I/71X+OKuItVL._AC_SL1500_.jpg', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('8476aa78-cbb9-46e4-a039-f470180be712', '88038014-2c91-47f6-a90a-74adf663eb97', 'https://m.media-amazon.com/images/I/81JrYIgLUlL._AC_SL1500_.jpg', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('12e63978-2014-4a2a-a726-2655ef54ef40', '88038014-2c91-47f6-a90a-74adf663eb97', 'https://m.media-amazon.com/images/I/71Z2W+g46ML._AC_SL1500_.jpg', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('fa7c6e84-190a-428c-a50c-1432528ec44f', '88038014-2c91-47f6-a90a-74adf663eb97', 'https://m.media-amazon.com/images/I/615sM+fd2RL._AC_SL1500_.jpg', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('9d4def29-29da-46fa-9373-5ec134e24afd', '88038014-2c91-47f6-a90a-74adf663eb97', 'https://m.media-amazon.com/images/I/71X+OKuItVL._AC_SL1500_.jpg', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('cc572182-a7eb-487c-bb25-3262ed8c3400', 'cb342ff9-fcc3-4c24-9845-b43f23856f7d', 'https://m.media-amazon.com/images/I/71X+OKuItVL._AC_SL1500_.jpg', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('0753d044-576b-4d39-8aaa-d191aeaeca42', 'cb342ff9-fcc3-4c24-9845-b43f23856f7d', 'https://m.media-amazon.com/images/I/61w7pG1uOAL._AC_SL1500_.jpg', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('2cd197ff-42e2-4b59-acfe-fb79ed482531', 'cb342ff9-fcc3-4c24-9845-b43f23856f7d', 'https://m.media-amazon.com/images/I/61wJeelYVaL._AC_SL1500_.jpg', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('22eaeef8-c702-47ed-967a-f6352ab0686e', 'cb342ff9-fcc3-4c24-9845-b43f23856f7d', 'https://m.media-amazon.com/images/I/81CpDU8Aw6L._AC_SL1500_.jpg', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('02fcb160-a180-4be5-9927-4ff7f83a474b', 'a04eb31b-cc1d-4cc4-a582-c362e32847a1', 'https://m.media-amazon.com/images/I/71X+OKuItVL._AC_SL1500_.jpg', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('47c31e83-ac70-4d2e-8293-07479b4269bd', 'a04eb31b-cc1d-4cc4-a582-c362e32847a1', 'https://m.media-amazon.com/images/I/61w7pG1uOAL._AC_SL1500_.jpg', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('b4257660-ac55-4d4a-b340-8a5add5c5567', 'a04eb31b-cc1d-4cc4-a582-c362e32847a1', 'https://m.media-amazon.com/images/I/61wJeelYVaL._AC_SL1500_.jpg', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('2ceace09-775e-4622-8a48-8c66b05ed1e4', 'a04eb31b-cc1d-4cc4-a582-c362e32847a1', 'https://m.media-amazon.com/images/I/81CpDU8Aw6L._AC_SL1500_.jpg', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('09be02c9-eba1-4ef1-bf5f-5bb0e967332f', 'f0d39eb0-91d8-400b-89c4-ee16724ea310', 'https://img01.ztat.net/article/spp-media-p1/e7d5535687874a518997522421231596/083a79b5507d44b0ba37460bf7169961.jpg?imwidth=1800', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('91ab4c82-0c24-488a-9e11-f99f1b47206b', 'f0d39eb0-91d8-400b-89c4-ee16724ea310', 'https://img01.ztat.net/article/spp-media-p1/1f4a7b35647145aa83949357c38ebbf7/184e264ef0b848928b4fc45d92604e13.jpg?imwidth=1800', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('2bc8def8-d4d0-433f-95cc-2c2374c06dc3', 'f0d39eb0-91d8-400b-89c4-ee16724ea310', 'https://img01.ztat.net/article/spp-media-p1/c61704b0794144afac449007b6094966/12449b06c0d04e9583adcc9cfe9d7b34.jpg?imwidth=1800', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('6f2fa1d5-a9ce-4c4d-8e63-dbc629a72583', 'f0d39eb0-91d8-400b-89c4-ee16724ea310', 'https://img01.ztat.net/article/spp-media-p1/dc190f0d0a57448699e86c73881391fe/4b1d1c00cc8c4d4183c5b010bf2413ec.jpg?imwidth=1800', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('884fad7a-0270-4962-989e-62498ecebaac', 'f0d39eb0-91d8-400b-89c4-ee16724ea310', 'https://img01.ztat.net/article/spp-media-p1/64f9e64b72fe4ffbabaa620816436caa/415c23d7a9b748c694e30b61a4dc1568.jpg?imwidth=1800', FALSE, '5', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('7f221126-a189-4fdd-97cb-5fa4c759178a', 'b275e98c-6afc-48c4-9bf2-0cd65d4686f4', 'https://img01.ztat.net/article/spp-media-p1/e7d5535687874a518997522421231596/083a79b5507d44b0ba37460bf7169961.jpg?imwidth=1800', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('b378c628-9801-4af3-a6db-4f4ce68f7854', 'b275e98c-6afc-48c4-9bf2-0cd65d4686f4', 'https://img01.ztat.net/article/spp-media-p1/1f4a7b35647145aa83949357c38ebbf7/184e264ef0b848928b4fc45d92604e13.jpg?imwidth=1800', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('97381f20-adb5-4700-8cd3-7af77a8865bc', 'b275e98c-6afc-48c4-9bf2-0cd65d4686f4', 'https://img01.ztat.net/article/spp-media-p1/c61704b0794144afac449007b6094966/12449b06c0d04e9583adcc9cfe9d7b34.jpg?imwidth=1800', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('68b38bc5-b2fe-44de-bf90-15674f783e88', 'b275e98c-6afc-48c4-9bf2-0cd65d4686f4', 'https://img01.ztat.net/article/spp-media-p1/dc190f0d0a57448699e86c73881391fe/4b1d1c00cc8c4d4183c5b010bf2413ec.jpg?imwidth=1800', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('dc6314cb-bff5-42de-852b-fb8ddd3f1ac2', 'b275e98c-6afc-48c4-9bf2-0cd65d4686f4', 'https://img01.ztat.net/article/spp-media-p1/64f9e64b72fe4ffbabaa620816436caa/415c23d7a9b748c694e30b61a4dc1568.jpg?imwidth=1800', FALSE, '5', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('779e786a-eec1-494b-a839-3eaba5f4a9d3', '7e383b81-de90-46c3-b152-e099b1b0b3fa', 'https://img01.ztat.net/article/spp-media-p1/e7d5535687874a518997522421231596/083a79b5507d44b0ba37460bf7169961.jpg?imwidth=1800', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('6e3d5b77-1fdc-4df1-b28f-e469ce8b0af9', '7e383b81-de90-46c3-b152-e099b1b0b3fa', 'https://img01.ztat.net/article/spp-media-p1/1f4a7b35647145aa83949357c38ebbf7/184e264ef0b848928b4fc45d92604e13.jpg?imwidth=1800', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('820208b2-2300-49c8-a10b-f58e397fe620', '7e383b81-de90-46c3-b152-e099b1b0b3fa', 'https://img01.ztat.net/article/spp-media-p1/c61704b0794144afac449007b6094966/12449b06c0d04e9583adcc9cfe9d7b34.jpg?imwidth=1800', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('7a99e72d-9cbb-4846-94ca-f1c32f6c67bc', '7e383b81-de90-46c3-b152-e099b1b0b3fa', 'https://img01.ztat.net/article/spp-media-p1/dc190f0d0a57448699e86c73881391fe/4b1d1c00cc8c4d4183c5b010bf2413ec.jpg?imwidth=1800', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('c723661d-a4e1-41f1-9154-0bfdd08e0fb2', '7e383b81-de90-46c3-b152-e099b1b0b3fa', 'https://img01.ztat.net/article/spp-media-p1/64f9e64b72fe4ffbabaa620816436caa/415c23d7a9b748c694e30b61a4dc1568.jpg?imwidth=1800', FALSE, '5', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('697d28f5-c66f-4be0-969a-cd134efcd09f', 'd9c617c0-645a-428b-9087-c06b903ba9ee', 'https://img01.ztat.net/article/spp-media-p1/e7d5535687874a518997522421231596/083a79b5507d44b0ba37460bf7169961.jpg?imwidth=1800', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('ea697139-d52a-4236-917d-4612731945f4', 'd9c617c0-645a-428b-9087-c06b903ba9ee', 'https://img01.ztat.net/article/spp-media-p1/1f4a7b35647145aa83949357c38ebbf7/184e264ef0b848928b4fc45d92604e13.jpg?imwidth=1800', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('f419e757-c09f-4bc1-ab4c-1f4aed09bfd8', 'd9c617c0-645a-428b-9087-c06b903ba9ee', 'https://img01.ztat.net/article/spp-media-p1/c61704b0794144afac449007b6094966/12449b06c0d04e9583adcc9cfe9d7b34.jpg?imwidth=1800', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('d2a666db-c2c1-4bfd-8454-1701e57daedd', 'd9c617c0-645a-428b-9087-c06b903ba9ee', 'https://img01.ztat.net/article/spp-media-p1/dc190f0d0a57448699e86c73881391fe/4b1d1c00cc8c4d4183c5b010bf2413ec.jpg?imwidth=1800', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('a7295678-b722-4048-907e-e42d689e19a4', 'd9c617c0-645a-428b-9087-c06b903ba9ee', 'https://img01.ztat.net/article/spp-media-p1/64f9e64b72fe4ffbabaa620816436caa/415c23d7a9b748c694e30b61a4dc1568.jpg?imwidth=1800', FALSE, '5', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('27ffb840-e56b-4073-9dfc-59d845794293', '67e9c8ef-8df6-49ab-ba09-3cc0f9bf3f31', 'https://img01.ztat.net/article/spp-media-p1/e7d5535687874a518997522421231596/083a79b5507d44b0ba37460bf7169961.jpg?imwidth=1800', TRUE, '1', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('d75247fc-c8e5-429f-a53f-8d16389be986', '67e9c8ef-8df6-49ab-ba09-3cc0f9bf3f31', 'https://img01.ztat.net/article/spp-media-p1/1f4a7b35647145aa83949357c38ebbf7/184e264ef0b848928b4fc45d92604e13.jpg?imwidth=1800', FALSE, '2', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('484d211b-1b8e-4d45-9065-32aaa6e07cb9', '67e9c8ef-8df6-49ab-ba09-3cc0f9bf3f31', 'https://img01.ztat.net/article/spp-media-p1/c61704b0794144afac449007b6094966/12449b06c0d04e9583adcc9cfe9d7b34.jpg?imwidth=1800', FALSE, '3', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('b856a028-6441-44d9-a65d-bf88097ebd65', '67e9c8ef-8df6-49ab-ba09-3cc0f9bf3f31', 'https://img01.ztat.net/article/spp-media-p1/dc190f0d0a57448699e86c73881391fe/4b1d1c00cc8c4d4183c5b010bf2413ec.jpg?imwidth=1800', FALSE, '4', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242'),
    ('dd690658-f622-4f9c-8ff7-48bae71ab75d', '67e9c8ef-8df6-49ab-ba09-3cc0f9bf3f31', 'https://img01.ztat.net/article/spp-media-p1/64f9e64b72fe4ffbabaa620816436caa/415c23d7a9b748c694e30b61a4dc1568.jpg?imwidth=1800', FALSE, '5', '2025-11-13 20:53:22.523242', '2025-11-13 20:53:22.523242');

INSERT INTO properties(id, category_id, name, unit, data_type, required, has_default_options)
VALUES
    ('522c10f4-91f8-4318-ac34-da186d71cb1d', 'b32cc5db-6d73-49ad-b076-574e741ec35c', 'Memory (RAM)', NULL, 'NUMBER', 'TRUE', 'FALSE'),
    ('94d9e569-9147-444b-bdb4-42a37534534d', 'b32cc5db-6d73-49ad-b076-574e741ec35c', 'Device storage', NULL, 'NUMBER', 'TRUE', 'FALSE'),
    ('6b33dc73-a7c5-4f1d-8b87-73c383dcbe81', 'b32cc5db-6d73-49ad-b076-574e741ec35c', 'Screen size range', NULL, 'TEXT', 'FALSE', 'TRUE');

INSERT INTO default_property_options(id, property_id, value_text, value_decimal, value_boolean, value_date, display_text)
VALUES
    ('e1e4e93e-7939-4de9-b982-b6028a1c0665', '6b33dc73-a7c5-4f1d-8b87-73c383dcbe81', NULL, NULL, NULL, NULL, '4'' - 5'''),
    ('c4abd317-9bfa-4265-84ec-076dc20a9a9f', '6b33dc73-a7c5-4f1d-8b87-73c383dcbe81', NULL, NULL, NULL, NULL, '5'' - 6'''),
    ('6ce59420-eec0-4208-8e46-0163f97842b7', '6b33dc73-a7c5-4f1d-8b87-73c383dcbe81', NULL, NULL, NULL, NULL, '6'' - 7''');

INSERT INTO variant_properties(id, variant_id, property_id, value_text, value_decimal, value_boolean, value_date, display_text)
VALUES
    ('b12e0a7c-c091-4158-83ff-f1a28dedbeca', '4d49fb76-2d75-4337-ac65-d05a6f8eabdb', '522c10f4-91f8-4318-ac34-da186d71cb1d', NULL, NULL, NULL, NULL, '8GB'),
    ('fcbff1e2-f7dd-4005-a90c-56432b1c7abb', '4d49fb76-2d75-4337-ac65-d05a6f8eabdb', '94d9e569-9147-444b-bdb4-42a37534534d', NULL, NULL, NULL, NULL, '256GB'),
    ('de986937-cb8e-4d2a-bca8-93a91988bc87', '4d49fb76-2d75-4337-ac65-d05a6f8eabdb', '94d9e569-9147-444b-bdb4-42a37534534d', NULL, NULL, NULL, NULL, '6'' - 7'''),
    ('8fcdc5b9-c4a0-436e-9625-0ca6060e8684', '88038014-2c91-47f6-a90a-74adf663eb97', '522c10f4-91f8-4318-ac34-da186d71cb1d', NULL, NULL, NULL, NULL, '16GB'),
    ('6debea44-dbd5-4f98-b1d3-2c2b443064cb', '88038014-2c91-47f6-a90a-74adf663eb97', '94d9e569-9147-444b-bdb4-42a37534534d', NULL, NULL, NULL, NULL, '512GB'),
    ('b30c6713-895e-4660-a1bf-2585cc4bf3e6', '88038014-2c91-47f6-a90a-74adf663eb97', '6b33dc73-a7c5-4f1d-8b87-73c383dcbe81', NULL, NULL, NULL, NULL, '6'' - 7''');

