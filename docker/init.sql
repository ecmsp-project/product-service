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
                            has_default_options boolean NOT NULL,
                            role text NOT NULL CHECK (role in ('INFO', 'REQUIRED', 'SELECTABLE')),
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
                                position int DEFAULT 1 NOT NULL,
                                is_main boolean DEFAULT FALSE NOT NULL,
                                created_at timestamp  NOT NULL,
                                updated_at timestamp  NOT NULL,
                                CONSTRAINT variant_images_pk PRIMARY KEY (id)
);

-- Table: variants
CREATE TABLE variants (
                          id uuid  NOT NULL,
                          product_id uuid  NOT NULL,
                          price decimal(12,2)  NOT NULL,
                          margin decimal(5,2)  NULL,
                          stock_quantity int  NOT NULL,
                          additional_properties jsonb  NULL,
                          description text  NULL,
                          created_at timestamp  NOT NULL,
                          updated_at timestamp  NOT NULL,
                          CONSTRAINT variants_pk PRIMARY KEY (id)
);

-- Table: deliveries
CREATE TABLE deliveries (
                            id uuid  NOT NULL,
                            recorded_at timestamp  NOT NULL,
                            CONSTRAINT deliveries_pk PRIMARY KEY (id)
);

-- Table: delivery_items
CREATE TABLE delivery_items (
                                 id uuid  NOT NULL,
                                 delivery_id uuid  NOT NULL,
                                 variant_id uuid  NOT NULL,
                                 quantity int  NOT NULL,
                                 CONSTRAINT delivery_items_pk PRIMARY KEY (id)
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

-- Reference: delivery_items_deliveries (table: delivery_items)
ALTER TABLE delivery_items ADD CONSTRAINT delivery_items_deliveries
    FOREIGN KEY (delivery_id)
        REFERENCES deliveries (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: delivery_items_variants (table: delivery_items)
ALTER TABLE delivery_items ADD CONSTRAINT delivery_items_variants
    FOREIGN KEY (variant_id)
        REFERENCES variants (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

CREATE OR REPLACE FUNCTION update_has_default_options()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE properties
        SET has_default_options = TRUE
        WHERE id = NEW.property_id;

CREATE TABLE kafka_outbox
(
    event_id     UUID PRIMARY KEY,
    payload      TEXT,
    created_at   TIMESTAMP    NOT NULL,
    processed    BOOLEAN      NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMP,
    event_type   TEXT
);

CREATE INDEX idx_kafka_outbox_processed ON kafka_outbox (processed);
CREATE INDEX idx_kafka_outbox_created_at ON kafka_outbox (created_at);
CREATE INDEX idx_kafka_outbox_processed_at ON kafka_outbox (processed_at) WHERE processed = TRUE;


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

    ELSIF TG_OP = 'DELETE' THEN
        IF NOT EXISTS (
            SELECT 1
            FROM default_property_options
            WHERE property_id = OLD.property_id
        ) THEN
            UPDATE properties
            SET has_default_options = FALSE
            WHERE id = OLD.property_id;
            END IF;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_has_default_options
AFTER INSERT OR DELETE ON default_property_options
FOR EACH ROW
EXECUTE FUNCTION update_has_default_options();
