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

CREATE TABLE temperature_translation (
  temp_id       INT NOT NULL,
  lang          VARCHAR(8) NOT NULL,
  name          VARCHAR(50) NOT NULL,
  symbol        VARCHAR(1) NOT NULL,

  FOREIGN KEY (temp_id) 
    REFERENCES temperature(id)
    ON DELETE CASCADE,
  UNIQUE INDEX(temp_id, lang)
);

INSERT INTO temperature_translation (temp_id, name, symbol, lang)
VALUES
    (1, 'Celsius', 'C', 'en'),
    (1, 'Celsius', 'C', 'fi'),
    (1, 'Celsius', 'C', 'sv'),
    (1, 'セルシウス', 'ど', 'ja'),
    (1, 'سيلسيوس', 'م', 'ar'),
    (2, 'فهرنهايت', 'ف', 'ar'),
    (2, 'Fahrenheit', 'F', 'en'),
    (2, 'Fahrenheit', 'F', 'fi'),
    (2, 'Fahrenheit', 'F', 'se'),
    (2, '華氏', 'F', 'ja');
