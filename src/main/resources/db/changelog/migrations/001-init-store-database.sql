--liquibase formatted sql
--changeset pvpeev:init-store-db

CREATE TABLE "product_category" (
  "id" smallserial PRIMARY KEY,
  "path" varchar(100) UNIQUE NOT NULL,
  "name" varchar(100) UNIQUE NOT NULL,
  "description" varchar(500) NOT NULL
);

CREATE TABLE "product_brand" (
  "id" smallserial PRIMARY KEY,
  "name" varchar(100) UNIQUE NOT NULL
);

CREATE TABLE "product_image" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "product_id" uuid NOT NULL,
  "image_url" varchar(1000) NOT NULL
);

CREATE TABLE "product" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "product_category_id" smallint NOT NULL,
  "product_brand_id" smallint NOT NULL,
  "name" varchar(100) UNIQUE NOT NULL,
  "description" varchar(700) NOT NULL,
  "thumbnail_image_url" varchar(1000) UNIQUE NOT NULL,
  "price" integer NOT NULL,
  "quantity_available" integer NOT NULL,
  "deleted" boolean NOT NULL
);

CREATE TABLE "order_status" (
  "id" uuid PRIMARY KEY,
  "order_id" uuid NOT NULL,
  "delivery_status" uuid NOT NULL,
  "status_update_date" timestamptz NOT NULL DEFAULT (now()),
  "status_description" varchar(1000) NOT NULL
);

CREATE TABLE "order" (
  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
  "user_id" uuid NOT NULL,
  "order_address" varchar(1000),
  "order_date" timestamptz NOT NULL DEFAULT (now()),
  "payment_type" smallint NOT NULL,
  "phone_number" varchar(20) NOT NULL,
  "tracking_code" varchar(150),
  "shipping_method" smallint NOT NULL
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
  "first_name" varchar(150),
  "last_name" varchar(150),
  "password" varchar NOT NULL,
  "phone_number" varchar(50),
  "account_expired" boolean NOT NULL DEFAULT false,
  "account_locked" boolean NOT NULL DEFAULT false,
  "credentials_expired" boolean NOT NULL DEFAULT false,
  "enabled" boolean NOT NULL
);

CREATE TABLE "user_address" (
  "id" bigserial PRIMARY KEY,
  "user_id" uuid NOT NULL,
  "address" varchar(1000) NOT NULL
);

CREATE TABLE "user_authority" (
  "id" bigserial PRIMARY KEY,
  "user_id" uuid NOT NULL,
  "authority_id" smallint NOT NULL
);

CREATE UNIQUE INDEX ON "product_image" ("product_id", "image_url");

CREATE INDEX ON "product" ("product_category_id", "product_brand_id");

CREATE INDEX ON "order" ("user_id");

CREATE UNIQUE INDEX ON "order_product" ("order_id", "product_id");

CREATE UNIQUE INDEX ON "user_address" ("user_id", "address");

CREATE UNIQUE INDEX ON "user_authority" ("user_id", "authority_id");

ALTER TABLE "product" ADD CONSTRAINT "fk_product_category" FOREIGN KEY ("product_category_id") REFERENCES "product_category" ("id");

ALTER TABLE "product" ADD CONSTRAINT "fk_product_brand" FOREIGN KEY ("product_brand_id") REFERENCES "product_brand" ("id");

ALTER TABLE "order_status" ADD CONSTRAINT "fk_order_id" FOREIGN KEY ("order_id") REFERENCES "order" ("id");

ALTER TABLE "order" ADD CONSTRAINT "fk_user_id" FOREIGN KEY ("user_id") REFERENCES "store_user" ("id");

ALTER TABLE "order_product" ADD CONSTRAINT "fk_order_id" FOREIGN KEY ("order_id") REFERENCES "order" ("id");

ALTER TABLE "order_product" ADD CONSTRAINT "fk_product_id" FOREIGN KEY ("product_id") REFERENCES "product" ("id");

ALTER TABLE "user_authority" ADD CONSTRAINT "fk_user_id" FOREIGN KEY ("user_id") REFERENCES "store_user" ("id");

ALTER TABLE "user_address" ADD CONSTRAINT "fk_address_id" FOREIGN KEY ("user_id") REFERENCES "store_user" ("id");

ALTER TABLE "product_image" ADD CONSTRAINT "fk_product_id" FOREIGN KEY ("product_id") REFERENCES "product" ("id");
