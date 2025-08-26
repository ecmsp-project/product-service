-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2025-07-31 16:47:43.113

-- tables
-- Table: attribute_values
CREATE TABLE attribute_values (
                                  id uuid  NOT NULL,
                                  attribute_id uuid  NOT NULL,
                                  value text  NOT NULL,
                                  CONSTRAINT attribute_values_pk PRIMARY KEY (id)
);

-- Table: attributes
CREATE TABLE attributes (
                            id uuid  NOT NULL,
                            category_id uuid  NOT NULL,
                            name varchar(255)  NOT NULL,
                            unit varchar(50)  NULL,
                            data_type text  NOT NULL CHECK ('text', 'number', 'boolean', 'date'),
    filterable boolean  NOT NULL,
    CONSTRAINT attributes_pk PRIMARY KEY (id)
);

-- Table: categories
CREATE TABLE categories (
                            id uuid  NOT NULL,
                            name varchar(255)  NOT NULL,
                            parent_category_id uuid  NULL,
                            CONSTRAINT categories_pk PRIMARY KEY (id)
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

-- Table: variant_attributes
CREATE TABLE variant_attributes (
                                    id uuid  NOT NULL,
                                    variant_id uuid  NOT NULL,
                                    attribute_id uuid  NOT NULL,
                                    attribute_value_id uuid  NULL,
                                    value_text text  NULL,
                                    value_decimal decimal(10,2)  NULL,
                                    value_boolean boolean  NULL,
                                    value_date date  NULL,
                                    CONSTRAINT variant_attributes_pk PRIMARY KEY (id)
);

-- Table: variants
CREATE TABLE variants (
                          id uuid  NOT NULL,
                          product_id uuid  NOT NULL,
                          sku char(10)  NOT NULL,
                          price decimal(12,2)  NOT NULL,
                          stock_quantity int  NOT NULL,
                          image_url varchar(255)  NOT NULL,
                          additional_attributes jsonb  NULL,
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

-- Reference: attribute_category (table: attributes)
ALTER TABLE attributes ADD CONSTRAINT attribute_category
    FOREIGN KEY (category_id)
        REFERENCES categories (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: attribute_values_attributes (table: attribute_values)
ALTER TABLE attribute_values ADD CONSTRAINT attribute_values_attributes
    FOREIGN KEY (attribute_id)
        REFERENCES attributes (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: variant_attribute_attribute (table: variant_attributes)
ALTER TABLE variant_attributes ADD CONSTRAINT variant_attribute_attribute
    FOREIGN KEY (attribute_id)
        REFERENCES attributes (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: variant_attribute_variant (table: variant_attributes)
ALTER TABLE variant_attributes ADD CONSTRAINT variant_attribute_variant
    FOREIGN KEY (variant_id)
        REFERENCES variants (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: variant_attributes_attribute_values (table: variant_attributes)
ALTER TABLE variant_attributes ADD CONSTRAINT variant_attributes_attribute_values
    FOREIGN KEY (attribute_value_id)
        REFERENCES attribute_values (id)
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

-- End of file.

