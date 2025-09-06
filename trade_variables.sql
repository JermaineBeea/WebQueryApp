-- Trade Variables Database Export
-- Generated on: Sat Sep 06 12:36:16 SAST 2025

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
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeprofit', 1000, 100, -0.07992, 0.1816363636182, -372.4, 8463.63636279);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('profitfactor', -0.000497021, -0.000497021, -0.000497021, -0.000497021, 0.00214715358615, 0.2147153598144);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('tradeamount', 50000, 5000, -2161103.56719081, -216110.356719081, -1343.98496240599, 5913.53383517787);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('buyvariable', 1.25, 1.1, 1.1505718584, 1.3006464486, 1.14810860131695, 1.07064999997125);
INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('sellvariable', 1.3, 1.15, 1.24937872375, 1.0994532769, 1.10181186894476, 1.517769199768);

-- End of export
