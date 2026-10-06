-- Keep historical provider enum values while allowing new VNPAY payments.
ALTER TABLE payment
    MODIFY COLUMN gateway ENUM ('RAZORPAY', 'STRIPE', 'VNPAY') NULL;
