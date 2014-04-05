INSERT INTO APP.CONFIGURATION (ID, CONFIGKEY, CONFIGVALUE) VALUES (50, 'mail-template-vacation-request-subject', 'New Request for Vacation from {0}');
INSERT INTO APP.CONFIGURATION (ID, CONFIGKEY, CONFIGVALUE) VALUES (51, 'mail-template-vacation-request-body', 'Hi {0}

you have a new request for vacation.

{1} wants {2,number,integer} day(s) vacation from {3,date} to {4,date}.

Please, go to {5}

Ciao,
     ConAIR');
INSERT INTO APP.CONFIGURATION (ID, CONFIGKEY, CONFIGVALUE) VALUES (52, 'mail-template-vacation-approved-subject', 'Your vacation request was approved');
INSERT INTO APP.CONFIGURATION (ID, CONFIGKEY, CONFIGVALUE) VALUES (53, 'mail-template-vacation-approved-body', 'Hi {0}

you vacation request from {1,date} to {2,date} was approved! :-)

Ciao,
     ConAIR');
INSERT INTO APP.CONFIGURATION (ID, CONFIGKEY, CONFIGVALUE) VALUES (54, 'mail-template-vacation-rejected-subject', 'Your vacation request was rejected');
INSERT INTO APP.CONFIGURATION (ID, CONFIGKEY, CONFIGVALUE) VALUES (55, 'mail-template-vacation-rejected-body', 'Hi {0}

you vacation request from {1,date} to {2,date} was rejected! :-(

Ciao,
     ConAIR');


INSERT INTO APP.CONFIGURATION (ID, CONFIGKEY, CONFIGVALUE) VALUES (40, 'app-url', 'http://conair.sourcecoding.com/conair/');


DROP TABLE APP.PERDIEM;
DROP TABLE APP.PERDIEMGROUP;
DROP TABLE APP.TRAVELEXPENSESRATE;

delete APP.CONFIGURATION where id = 12;
INSERT INTO APP.CONFIGURATION(id, configkey, configvalue) VALUES(12, 'xls-template-path-for-travel-costs', 'https://onedrive.live.com/download?resid=6F3F709C6FABCA05!5596&authkey=!AIdSdDjAVrfjyHc&ithint=file%2c.xls')