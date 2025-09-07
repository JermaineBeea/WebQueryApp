-- WebAppDataBase Export
-- Generated on: Sun Sep 07 13:40:09 UTC 2025

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
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', 500, -103, -0.1135135135, 0.1272727273, -2009.81351327425, 112671.363660508);
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', 0.1, -0.05, -0.05, 0.1, -0.000116348, 0.0282398125);
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 50000, 1000, -564.7962497529, -58.1740137245, 51.2485359063, 221.8842409268);
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 18.5, 16.5, 14.9090909091, 19.5789473684, 16.4019083292, 18.0891653619);
INSERT INTO WebAppDataBase (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 18.6, 16.4, 17.575, 18.15, 16.498080258, 19.02243653125);

-- End of export
