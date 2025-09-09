-- WebAppDataBase Export
-- Generated on: Tue Sep 09 09:06:52 SAST 2025

DROP TABLE IF EXISTS WebAppDataBase;

CREATE TABLE WebAppDataBase (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- Insert data
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('tradeprofit', 0, 0, 0, 0);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('profitfactor', 0, 0, 0, 0);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('tradeamount', 0, 0, 0, 0);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('buyvariable', 0, 0, 0, 0);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('sellvariable', 0, 0, 0, 0);

-- End of export
