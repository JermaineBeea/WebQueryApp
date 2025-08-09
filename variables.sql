-- Variables Database Export
-- Generated on: Sat Aug 09 19:46:58 UTC 2025

DROP TABLE IF EXISTS variables;

CREATE TABLE variables (
    variable VARCHAR(10) DEFAULT '0',
    value INTEGER DEFAULT 0,
    query INTEGER DEFAULT 0
);

-- Insert data
INSERT INTO variables (variable, value, query) VALUES ('a', 1, -75);
INSERT INTO variables (variable, value, query) VALUES ('b', 2, 78);
INSERT INTO variables (variable, value, query) VALUES ('c', -77, -1);

-- End of export
