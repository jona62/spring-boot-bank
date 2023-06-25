CREATE TABLE IF NOT EXISTS Bank (
    accountNumber VARCHAR(10) PRIMARY KEY,
    trust DOUBLE PRECISION NOT NULL,
    transactionFee INTEGER NOT NULL
);

-- INSERT INTO Bank (accountNumber, trust, transactionFee)
-- VALUES ('1234567890', 500.0, 3);
--
-- INSERT INTO Bank (accountNumber, trust, transactionFee)
-- VALUES ('567890', 400.0, 10);

SELECT * FROM Bank;