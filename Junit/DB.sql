CREATE DATABASE IF NOT EXISTS temp_temperature CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE temp_temperature;

DROP TABLE temp_record;
DROP TABLE temperature;
CREATE TABLE temperature (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(16) NOT NULL,
    symbol      VARCHAR(1) NOT NULL
  );

INSERT INTO temperature (name, symbol)
VALUES
    ('Celsius', 'C'),
    ('Fahrenheit', 'F');

CREATE TABLE temp_record (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    input_value   DOUBLE NOT NULL,
    output_value  DOUBLE NOT NULL,
    from_unit_id  INT NOT NULL,
    to_unit_id    INT NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (from_unit_id) REFERENCES temperature(id),
    FOREIGN KEY (to_unit_id) REFERENCES temperature(id)
);


