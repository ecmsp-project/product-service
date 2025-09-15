-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2025-09-15 18:21:26.267

-- tables
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

-- Table: properties
CREATE TABLE properties (
                            id uuid  NOT NULL,
                            category_id uuid  NOT NULL,
                            name varchar(255)  NOT NULL,
                            unit varchar(50)  NULL,
                            data_type text  NOT NULL CHECK (data_type IN ('TEXT', 'NUMBER', 'BOOLEAN', 'DATE')),
    required boolean  NOT NULL,
    filterable boolean  NOT NULL,
    CONSTRAINT properties_pk PRIMARY KEY (id)
);

-- Table: property_options
CREATE TABLE property_options (
                                  id uuid  NOT NULL,
                                  property_id uuid  NOT NULL,
                                  value_text text  NULL,
                                  value_decimal decimal(10,2)  NULL,
                                  value_boolean boolean  NULL,
                                  value_date date  NULL,
                                  display_text text  NOT NULL,
                                  CONSTRAINT property_options_pk PRIMARY KEY (id)
);

-- Table: variant_properties
CREATE TABLE variant_properties (
                                    id uuid  NOT NULL,
                                    variant_id uuid  NOT NULL,
                                    property_id uuid  NOT NULL,
                                    property_option_id uuid  NULL,
                                    custom_value_text text  NULL,
                                    custom_value_decimal decimal(10,2)  NULL,
                                    custom_value_boolean boolean  NULL,
                                    custom_value_date date  NULL,
                                    CONSTRAINT variant_properties_pk PRIMARY KEY (id)
);

-- Table: variants
CREATE TABLE variants (
                          id uuid  NOT NULL,
                          product_id uuid  NOT NULL,
                          sku varchar(10)  NOT NULL,
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

-- Reference: attribute_values_attributes (table: property_options)
ALTER TABLE property_options ADD CONSTRAINT attribute_values_attributes
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

-- Reference: variant_attributes_attribute_values (table: variant_properties)
ALTER TABLE variant_properties ADD CONSTRAINT variant_attributes_attribute_values
    FOREIGN KEY (property_option_id)
        REFERENCES property_options (id)
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

