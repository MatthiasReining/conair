--alter since 30.11.2013
ALTER TABLE PROJECTINFORMATION ADD COLUMN INVOICETEMPLATEURL VARCHAR(255);

--add an example invoice template URL
UPDATE PROJECTINFORMATION SET INVOICETEMPLATEURL = 'https://skydrive.live.com/download?resid=6F3F709C6FABCA05!5055&authkey=!ABPdEE6zKQC6I_I&ithint=file%2c.xls';


INSERT INTO CONFIGURATION(ID, CONFIGKEY, CONFIGVALUE) VALUES(10, 'xls-template-path-for-vacation','https://skydrive.live.com/download?resid=6F3F709C6FABCA05!5100&authkey=!AIhmqIbsuhYZJXw&ithint=file%2c.xls');
INSERT INTO CONFIGURATION(ID, CONFIGKEY, CONFIGVALUE) VALUES(11, 'xls-template-path-for-vacation-overview','https://skydrive.live.com/download?resid=6F3F709C6FABCA05!5101&authkey=!AGZDGsxGU9GVHto&ithint=file%2c.xls');