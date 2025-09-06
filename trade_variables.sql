-- Trade Variables Database Export
-- Generated on: Sat Sep 06 10:31:51 SAST 2025

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
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', 2, 3, 0, 0, 0, 0);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', -1, 1, 1, -1, 3.2223415682, 2.1482277121);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 1, 1, 0, 0, 0, 0);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 1, 1, 0, 0, 0, 0);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 1, 1, 2, 0, 4.2223415682, 3.1482277121);

-- End of export
