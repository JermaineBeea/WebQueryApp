DROP TABLE IF EXISTS ConsoleDataBase;

CREATE TABLE ConsoleDataBase (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    factormin DECIMAL(20,8) DEFAULT 0,
    factormax DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- Insert data
INSERT INTO ConsoleDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', -88, -88, -0.0004970207, -0.0004970207, -88.0000000385, -88.0000000385);
INSERT INTO ConsoleDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 10000, 10000, 9999.9939596627, 9999.9939596627, 9999.999995625, 9999.999995625);
INSERT INTO ConsoleDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 17.7055, 17.7055, 17.7055000053, 17.7055000053, 17.7055, 17.7055);
INSERT INTO ConsoleDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 17.6967, 17.6967, 17.6966999946845, 17.6966999946845, 17.6966999999961, 17.6966999999961);
INSERT INTO ConsoleDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', -0.000497021, -0.000497021, -0.000497021, -0.000497021, -0.0004970207, -0.0004970207);

-- End of export
