-- WebAppDataBase Export
-- Generated on: Sun Sep 07 15:38:25 UTC 2025

DROP TABLE IF EXISTS WebAppDataBase;

CREATE TABLE WebAppDataBase (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    factormin DECIMAL(20,8) DEFAULT 0,
    factormax DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- Insert data
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', -88, -88, 0.0004972679, 0.0004972679, 88.0437680345, 88.0437680345);
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', -0.000497021, -0.000497021, -0.000497021, -0.000497021, -0.0004970207, -0.0004970207);
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 10000, 10000, 9999.9939596627, 9999.9939596627, -9995.028832196, -9995.028832196);
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 17.6967, 17.6967, 17.7143043813, 17.7143043813, 17.714304376, 17.714304376);
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 17.7055, 17.7055, 17.6879043684693, 17.6879043684693, 17.6879043737783, 17.6879043737783);

-- End of export
