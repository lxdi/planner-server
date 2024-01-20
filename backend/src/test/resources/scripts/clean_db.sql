
SET REFERENTIAL_INTEGRITY FALSE;

delete from repetitions;
--delete from repetitionplan;
delete from task_mappers;

delete from topics;
delete from task_testings;
delete from tasks;
delete from layers;
delete from means;
delete from realms;
delete from weeks;
delete from days;

SET REFERENTIAL_INTEGRITY TRUE;
--
--ALTER TABLE repetition ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE spacedrepetitions ALTER COLUMN id RESTART WITH 1;
----ALTER TABLE repetitionplan ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE mapperexclusion ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE taskmapper ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE slotposition ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE slot ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE hquarter ALTER COLUMN id RESTART WITH 1;
--
--ALTER TABLE topic ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE tasktesting ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE task ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE layer ALTER COLUMN id RESTART WITH 1;
----ALTER TABLE mean_target ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE mean ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE target ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE realm ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE week ALTER COLUMN id RESTART WITH 1;