DROP TABLE IF EXISTS variables;

CREATE TABLE variables (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    factormin DECIMAL(20,8) DEFAULT 0,
    factormax DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- Insert data
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', 10000, 5000, 0, 0, 0, 0);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 50000, 25000, 0, 0, 0, 0);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 0, 0, 0, 0, 0, 0);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 0, 0, 0, 0, 0, 0);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', 2, 4, 0, 0, 0, 0);

-- End of export
