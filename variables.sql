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
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', -88, -88, -0.0004970207, -0.0004970207, -4.627262717, -4.627262717);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 10000, 10000, 190177.113912791, 190177.113912791, 190177.228703049, 190177.228703049);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 17.7055, 17.7055, 17.7055000053, 17.7055000053, 17.8655689649, 17.8655689649);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 17.6967, 17.6967, 17.6966999946845, 17.6966999946845, 17.5381440392595, 17.5381440392595);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', -0.000497021, -0.000497021, -0.000497021, -0.000497021, -0.0094522019, -0.0094522019);

-- End of export
