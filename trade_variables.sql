-- Trade Variables Database Export
-- Generated on: Sat Sep 06 14:40:29 SAST 2025

DROP TABLE IF EXISTS trade_variables;

CREATE TABLE trade_variables (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    factormin DECIMAL(20,8) DEFAULT 0,
    factormax DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- Insert data
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', 1000, 100, -0.08, 0.1818181818, -372.4, 8463.63636279);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', 0, 0, 0, 0, 0.0021482277, 0.2148227712);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 50000, 5000, 0, 0, 0, 0);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 1.25, 1.1, 0, 0, 0, 0);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 1.3, 1.15, 0, 0, 0, 0);

-- End of export
