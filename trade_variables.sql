-- Trade Variables Database Export
-- Generated on: Sat Sep 06 23:12:32 UTC 2025

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
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', 10000, 10000, -0.0004970207, -0.0004970207, -88.0000000385, -88.0000000385);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', -0.000497021, -0.000497021, -0.000497021, -0.000497021, 0.056479625, 0.056479625);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 10000, 10000, -1136362.94996167, -1136362.94996167, -1136363.63586648, -1136363.63586648);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 17.7055, 17.7055, 17.7055000053, 17.7055000053, 16.7506306617, 16.7506306617);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 17.6967, 17.6967, 17.6966999946845, 17.6966999946845, 18.7055000004375, 18.7055000004375);

-- End of export
