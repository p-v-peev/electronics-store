ALTER TABLE public.order
ADD CONSTRAINT check_shipping_method CHECK(shipping_method IN(1,2));

ALTER TABLE public.order
ADD CONSTRAINT check_payment_type CHECK(payment_type IN(1,2));

ALTER TABLE order_status
ADD CONSTRAINT check_order_status CHECK(order_status IN (100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300))