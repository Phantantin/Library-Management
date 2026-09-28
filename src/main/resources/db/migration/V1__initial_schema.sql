CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `created_at` DATETIME(6) NULL,
    `last_login` DATETIME(6) NULL,
    `updated_at` DATETIME(6) NULL,
    `email` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(255) NULL,
    `google_id` VARCHAR(255) NULL,
    `password` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(255) NULL,
    `profile_image` VARCHAR(255) NULL,
    `user_name` VARCHAR(255) NULL,
    `auth_provider` ENUM ('GOOGLE', 'LOCAL') NULL,
    `role` ENUM ('ROLE_ADMIN', 'ROLE_USER') NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_user_email` UNIQUE (`email`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `genre` (
    `active` BOOLEAN NOT NULL,
    `display_order` INT NULL,
    `created_at` DATETIME(6) NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `parent_genre_id` BIGINT NULL,
    `updated_at` DATETIME(6) NULL,
    `description` VARCHAR(500) NULL,
    `code` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_genre_code` UNIQUE (`code`),
    CONSTRAINT `fk_genre_parent` FOREIGN KEY (`parent_genre_id`) REFERENCES `genre` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `subscription_plan` (
    `display_order` INT NULL,
    `duration_days` INT NOT NULL,
    `is_active` BOOLEAN NULL,
    `is_featured` BOOLEAN NULL,
    `max_books_allowed` INT NOT NULL,
    `max_days_per_book` INT NOT NULL,
    `created_at` DATETIME(6) NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `price` BIGINT NOT NULL,
    `updated_at` DATETIME(6) NULL,
    `name` VARCHAR(100) NOT NULL,
    `admin_notes` VARCHAR(255) NULL,
    `badge_text` VARCHAR(255) NULL,
    `created_by` VARCHAR(255) NULL,
    `currency` VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    `plan_code` VARCHAR(255) NOT NULL,
    `updated_by` VARCHAR(255) NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_subscription_plan_code` UNIQUE (`plan_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `book` (
    `active` BOOLEAN NOT NULL,
    `available_copies` INT NOT NULL,
    `featured` BOOLEAN NULL,
    `pages` INT NULL,
    `price` DECIMAL(38, 2) NULL,
    `publicshe_date` DATE NULL,
    `total_copies` INT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `genre_id` BIGINT NOT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `updated_at` DATETIME(6) NOT NULL,
    `author` VARCHAR(255) NOT NULL,
    `cover_image_url` VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    `isbn` VARCHAR(255) NOT NULL,
    `language` VARCHAR(255) NULL,
    `publisher` VARCHAR(255) NULL,
    `title` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_book_isbn` UNIQUE (`isbn`),
    CONSTRAINT `fk_book_genre` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`id`),
    CONSTRAINT `ck_book_copies` CHECK (`available_copies` >= 0 AND `total_copies` >= `available_copies`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `password_reset_token` (
    `expiry_date` DATETIME(6) NOT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `token` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_password_reset_token` UNIQUE (`token`),
    CONSTRAINT `fk_password_reset_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `book_loan` (
    `checkout_date` DATE NOT NULL,
    `due_date` DATE NULL,
    `is_overdue` BOOLEAN NOT NULL,
    `max_renewals` INT NOT NULL,
    `overdue_days` INT NOT NULL,
    `return_count` INT NOT NULL,
    `return_date` DATE NULL,
    `book_id` BIGINT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `updated_at` DATETIME(6) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `notes` VARCHAR(500) NULL,
    `status` ENUM ('CHECKED_OUT', 'DAMAGED', 'LOST', 'OVERDUE', 'RETURNED') NOT NULL,
    `type` ENUM ('CHECKOUT', 'RENEWAL', 'RETURN') NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_book_loan_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_book_loan_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    INDEX `idx_book_loan_user_status` (`user_id`, `status`),
    INDEX `idx_book_loan_due_status` (`due_date`, `status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `book_review` (
    `rating` INT NOT NULL,
    `book_id` BIGINT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `updated_at` DATETIME(6) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `review_text` VARCHAR(255) NOT NULL,
    `title` VARCHAR(255) NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_book_review_user_book` UNIQUE (`user_id`, `book_id`),
    CONSTRAINT `fk_book_review_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_book_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `ck_book_review_rating` CHECK (`rating` BETWEEN 1 AND 5)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `reservation` (
    `notification_sent` BOOLEAN NOT NULL,
    `queue_position` INT NULL,
    `available_at` DATETIME(6) NULL,
    `available_until` DATETIME(6) NULL,
    `book_id` BIGINT NULL,
    `cancelled_at` DATETIME(6) NULL,
    `created_at` DATETIME(6) NOT NULL,
    `fulfilled_at` DATETIME(6) NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `reserved_at` DATETIME(6) NULL,
    `updated_at` DATETIME(6) NULL,
    `user_id` BIGINT NULL,
    `notes` TEXT NULL,
    `status` ENUM ('AVAILABLE', 'CANCELLED', 'EXPIRED', 'FULFILLED', 'PENDING') NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_reservation_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    INDEX `idx_reservation_book_status_queue` (`book_id`, `status`, `queue_position`),
    INDEX `idx_reservation_user_status` (`user_id`, `status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `wishlist` (
    `added_at` DATETIME(6) NULL,
    `book_id` BIGINT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NULL,
    `notes` VARCHAR(500) NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_wishlist_user_book` UNIQUE (`user_id`, `book_id`),
    CONSTRAINT `fk_wishlist_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_wishlist_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `subscription` (
    `auto_renew` BOOLEAN NULL,
    `end_date` DATE NOT NULL,
    `is_active` BOOLEAN NOT NULL,
    `max_books_allowed` INT NOT NULL,
    `max_days_per_book` INT NOT NULL,
    `start_date` DATE NOT NULL,
    `cancelled_at` DATETIME(6) NULL,
    `created_at` DATETIME(6) NOT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `plan_id` BIGINT NOT NULL,
    `price` BIGINT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `cancellation_reason` VARCHAR(255) NULL,
    `notes` VARCHAR(255) NULL,
    `plan_code` VARCHAR(255) NULL,
    `plan_name` VARCHAR(255) NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_subscription_plan` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plan` (`id`),
    CONSTRAINT `fk_subscription_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    INDEX `idx_subscription_user_active` (`user_id`, `is_active`, `start_date`, `end_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `fine` (
    `amount` BIGINT NOT NULL,
    `book_loan_id` BIGINT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `paid_at` DATETIME(6) NULL,
    `processed_by_user_id` BIGINT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `waived_at` DATETIME(6) NULL,
    `waived_by_id` BIGINT NULL,
    `transaction_id` VARCHAR(100) NULL,
    `reason` VARCHAR(500) NULL,
    `waiver_reason` VARCHAR(500) NULL,
    `notes` VARCHAR(1000) NULL,
    `status` ENUM ('PAID', 'PARTIALLY_PAID', 'PENDING', 'WAIVED') NULL,
    `type` ENUM ('DAMAGE', 'LOSS', 'OVERDUE', 'PROCESSING') NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_fine_book_loan` FOREIGN KEY (`book_loan_id`) REFERENCES `book_loan` (`id`),
    CONSTRAINT `fk_fine_processed_by` FOREIGN KEY (`processed_by_user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_fine_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_fine_waived_by` FOREIGN KEY (`waived_by_id`) REFERENCES `user` (`id`),
    INDEX `idx_fine_user_status` (`user_id`, `status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `payment` (
    `amount` BIGINT NULL,
    `completed_at` DATETIME(6) NULL,
    `created_at` DATETIME(6) NULL,
    `fine_id` BIGINT NULL,
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `initiate_at` DATETIME(6) NULL,
    `subscription_id` BIGINT NULL,
    `updated_at` DATETIME(6) NULL,
    `user_id` BIGINT NULL,
    `currency` VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    `failure_reason` VARCHAR(255) NULL,
    `gateway_order_id` VARCHAR(255) NULL,
    `gateway_payment_id` VARCHAR(255) NULL,
    `gateway_signature` VARCHAR(255) NULL,
    `transaction_id` VARCHAR(255) NULL,
    `gateway` ENUM ('RAZORPAY', 'STRIPE') NULL,
    `payment_type` ENUM ('DAMAGED_BOOK_PENALTY', 'FINE', 'LOST_BOOK_PENALTY', 'MEMBERSHIP', 'REFUND') NULL,
    `status` ENUM ('CANCELLED', 'FAILED', 'PENDING', 'PROCESSING', 'REFUNDED', 'SUCCESS') NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_payment_fine` FOREIGN KEY (`fine_id`) REFERENCES `fine` (`id`),
    CONSTRAINT `fk_payment_subscription` FOREIGN KEY (`subscription_id`) REFERENCES `subscription` (`id`),
    CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    INDEX `idx_payment_user_created` (`user_id`, `created_at`),
    INDEX `idx_payment_gateway_id` (`gateway_payment_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `genre_sub_genres` (
    `genre_id` BIGINT NOT NULL,
    `sub_genres_id` BIGINT NOT NULL,
    PRIMARY KEY (`genre_id`, `sub_genres_id`),
    CONSTRAINT `uk_genre_sub_genre` UNIQUE (`sub_genres_id`),
    CONSTRAINT `fk_genre_sub_genres_owner` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`id`),
    CONSTRAINT `fk_genre_sub_genres_child` FOREIGN KEY (`sub_genres_id`) REFERENCES `genre` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
