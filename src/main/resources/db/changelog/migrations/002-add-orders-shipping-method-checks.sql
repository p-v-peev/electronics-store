ALTER TABLE public.order
ADD CONSTRAINT check_shipping_method CHECK(shipping_method IN(1,2));

ALTER TABLE public.order
ADD CONSTRAINT check_payment_type CHECK(payment_type IN(1,2));