-- WebAppDataBase Export
-- Generated on: Sun Sep 07 17:42:13 UTC 2025

DROP TABLE IF EXISTS WebAppDataBase;

CREATE TABLE WebAppDataBase (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- Insert data
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('tradeprofit', -88, -88, -88.0000000385, -88.0000000385);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('profitfactor', -0.000497021, -0.000497021, -0.0004970207, -0.0004970207);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('tradeamount', 10000, 10000, 9999.999995625, 9999.999995625);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('buyvariable', 17.7055, 17.7055, 17.7055, 17.7055);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('sellvariable', 17.6967, 17.6967, 17.6966999999961, 17.6966999999961);

-- End of export
