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
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', 1000, 100, -0.08, 0.1818181818, -372.4, 8463.63636279);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 50000, 5000, 268.5284640172, 53.7056928034, -1342.6423200859, 5907.6262089689);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 1.25, 1.1, 0.3833333333, 0.26, 1.1475348339, 1.0701149425);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 1.3, 1.15, 6.25, 3.3, 1.10236305047, 1.518528464);
INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', 2, 4, 4, 2, 0.0021482277, 0.2148227712);

-- End of export
