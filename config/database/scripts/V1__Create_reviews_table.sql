-- -----------------------------------------------------
-- Table `reviews`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id            VARCHAR(50) PRIMARY KEY,
    review        TEXT NOT NULL,
    author        VARCHAR(255) NOT NULL,
    review_source VARCHAR(50) NOT NULL, -- Google Play or iTunes
    rating        INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
    title         VARCHAR(255),
    product_name  VARCHAR(255) NOT NULL,
    reviewed_date DATE NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO reviews (id, review, author, review_source, rating, title, product_name, reviewed_date) 
VALUES ('123e4567-e89b-12d3-a456-426614174000', 
        'Pero deberia de poder cambiarle el idioma a Alexa', 
        'WarcryxD', 
        'iTunes', 
        4, 
        'Excelente', 
        'Amazon Alexa', 
        '2018-01-12');
