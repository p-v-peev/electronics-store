--liquibase formatted sql
--changeset pvpeev:init-store-database

CREATE TABLE "product_category" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "name" varchar(100) NOT NULL,
  "description" varchar(500) NOT NULL,
  "image_url" varchar(1000) NOT NULL
);

CREATE TABLE "product_brand" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "name" varchar(100) NOT NULL
);

CREATE TABLE "product_image" (
  "id" uuid PRIMARY KEY,
  "product_id" uuid NOT NULL,
  "image_url" varchar(1000) NOT NULL
);

CREATE TABLE "product" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "product_category_id" uuid NOT NULL,
  "product_brand_id" uuid NOT NULL,
  "name" varchar(100) NOT NULL,
  "description" varchar(700) NOT NULL,
  "price" integer NOT NULL,
  "quantity_available" integer NOT NULL
);

CREATE TABLE "payment_type" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "name" varchar(100) NOT NULL,
  "description" varchar(700) NOT NULL
);

CREATE TABLE "order_status" (
  "id" uuid PRIMARY KEY,
  "order_id" uuid NOT NULL,
  "delivery_status_id" uuid NOT NULL,
  "status_update_date" timestamptz NOT NULL DEFAULT (now()),
  "status_description" varchar(1000) NOT NULL
);

CREATE TABLE "delivery_status" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "name" varchar(50) UNIQUE NOT NULL
);

CREATE TABLE "order" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "user_id" uuid NOT NULL,
  "order_address" varchar(1000),
  "order_zip_code" varchar(100),
  "order_date" timestamptz NOT NULL DEFAULT (now()),
  "payment_type_id" uuid NOT NULL,
  "phone_number" varchar(20) NOT NULL,
  "tracking_code" varchar(150),
  "shipping_method_id" uuid NOT NULL
);

CREATE TABLE "shipping_method" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "name" varchar(200) NOT NULL
);

CREATE TABLE "order_product" (
  "id" uuid PRIMARY KEY,
  "order_id" uuid NOT NULL,
  "product_id" uuid NOT NULL,
  "quantity" integer NOT NULL,
  "price_at_purchase" integer NOT NULL
);

CREATE TABLE "store_user" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "email" varchar(150) UNIQUE NOT NULL,
  "first_name" varchar(150) NOT NULL,
  "last_name" varchar(150) NOT NULL,
  "password" varchar NOT NULL,
  "phone_number" varchar(50),
  "account_expired" boolean NOT NULL,
  "account_locked" boolean NOT NULL,
  "credentials_expired" boolean NOT NULL,
  "enabled" boolean NOT NULL
);

CREATE TABLE "user_address" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "user_id" uuid NOT NULL,
  "address" varchar(1000) NOT NULL,
  "zip_code" varchar(100) NOT NULL
);

CREATE TABLE "authority" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "name" varchar(200) UNIQUE NOT NULL,
  "description" varchar(1000) UNIQUE NOT NULL
);

CREATE TABLE "user_authority" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "user_id" uuid NOT NULL,
  "authority_id" uuid NOT NULL
);

CREATE UNIQUE INDEX ON "product_image" ("product_id", "image_url");

CREATE UNIQUE INDEX ON "order_product" ("order_id", "product_id");

CREATE UNIQUE INDEX ON "user_address" ("user_id", "address", "zip_code");

CREATE UNIQUE INDEX ON "user_authority" ("user_id", "authority_id");

ALTER TABLE "product" ADD CONSTRAINT "fk_product_category" FOREIGN KEY ("product_category_id") REFERENCES "product_category" ("id");

ALTER TABLE "product" ADD CONSTRAINT "fk_product_brand" FOREIGN KEY ("product_brand_id") REFERENCES "product_brand" ("id");

ALTER TABLE "order" ADD CONSTRAINT "fk_payment_type" FOREIGN KEY ("payment_type_id") REFERENCES "payment_type" ("id");

ALTER TABLE "order_status" ADD CONSTRAINT "fk_order_id" FOREIGN KEY ("order_id") REFERENCES "order" ("id");

ALTER TABLE "order" ADD CONSTRAINT "fk_user_id" FOREIGN KEY ("user_id") REFERENCES "store_user" ("id");

ALTER TABLE "order" ADD CONSTRAINT "fk_shipping_method" FOREIGN KEY ("shipping_method_id") REFERENCES "shipping_method" ("id");

ALTER TABLE "order_product" ADD CONSTRAINT "fk_order_id" FOREIGN KEY ("order_id") REFERENCES "order" ("id");

ALTER TABLE "order_product" ADD CONSTRAINT "fk_product_id" FOREIGN KEY ("product_id") REFERENCES "product" ("id");

ALTER TABLE "user_authority" ADD CONSTRAINT "fk_user_id" FOREIGN KEY ("user_id") REFERENCES "store_user" ("id");

ALTER TABLE "user_authority" ADD CONSTRAINT "fk_authority_id" FOREIGN KEY ("authority_id") REFERENCES "authority" ("id");

ALTER TABLE "user_address" ADD CONSTRAINT "fk_address_id" FOREIGN KEY ("user_id") REFERENCES "store_user" ("id");

ALTER TABLE "product_image" ADD CONSTRAINT "fk_product_id" FOREIGN KEY ("product_id") REFERENCES "product" ("id");

ALTER TABLE "order_status" ADD CONSTRAINT "fk_delivery_status_id" FOREIGN KEY ("delivery_status_id") REFERENCES "delivery_status" ("id");
