ALTER TABLE repetitionplan ADD daystep BOOLEAN DEFAULT false;

ALTER TABLE layer ADD depth INTEGER DEFAULT 0;
UPDATE layer SET depth = priority;
UPDATE layer SET priority = 0;