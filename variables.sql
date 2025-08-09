-- Variables Database Export
-- Generated on: Fri Aug 08 16:47:24 SAST 2025

DROP TABLE IF EXISTS variables;

CREATE TABLE variables (
    variable VARCHAR(10) DEFAULT '0',
    value INTEGER DEFAULT 0,
    query INTEGER DEFAULT 0
);

-- Insert data
INSERT INTO variables (variable, value, query) VALUES ('a', 5, 9);
INSERT INTO variables (variable, value, query) VALUES ('b', 4, 0);
INSERT INTO variables (variable, value, query) VALUES ('c', 5, 1);

-- End of export
