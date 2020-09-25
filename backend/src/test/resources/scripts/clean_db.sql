
SET REFERENTIAL_INTEGRITY FALSE;

delete from repetition;
delete from spacedrepetitions;
--delete from repetitionplan;
delete from mapperexclusion;
delete from taskmapper;
delete from slotposition;
delete from slot;
delete from hquarter;

delete from topic;
delete from tasktesting;
delete from task;
delete from subject;
delete from layer;
delete from mean_target;
delete from mean;
delete from target;
delete from realm;
delete from week;

SET REFERENTIAL_INTEGRITY TRUE;

ALTER TABLE repetition ALTER COLUMN id RESTART WITH 1;
ALTER TABLE spacedrepetitions ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE repetitionplan ALTER COLUMN id RESTART WITH 1;
ALTER TABLE mapperexclusion ALTER COLUMN id RESTART WITH 1;
ALTER TABLE taskmapper ALTER COLUMN id RESTART WITH 1;
ALTER TABLE slotposition ALTER COLUMN id RESTART WITH 1;
ALTER TABLE slot ALTER COLUMN id RESTART WITH 1;
ALTER TABLE hquarter ALTER COLUMN id RESTART WITH 1;

ALTER TABLE topic ALTER COLUMN id RESTART WITH 1;
ALTER TABLE tasktesting ALTER COLUMN id RESTART WITH 1;
ALTER TABLE task ALTER COLUMN id RESTART WITH 1;
ALTER TABLE layer ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE mean_target ALTER COLUMN id RESTART WITH 1;
ALTER TABLE mean ALTER COLUMN id RESTART WITH 1;
ALTER TABLE target ALTER COLUMN id RESTART WITH 1;
ALTER TABLE realm ALTER COLUMN id RESTART WITH 1;
ALTER TABLE week ALTER COLUMN id RESTART WITH 1;