--legal holidays are from http://www.feiertage.net/frei-tage.php
--regex (notepad++) search (\d\d)\.(\d\d)\.(\d\d\d\d)  replace $3-$2-$1

DELETE from app.legalholiday;
--regex (notepad++) search (\d\d)\.(\d\d)\.(\d\d\d\d)  replace $3-$2-$1
--regex (notepad++) search (\d\d)\.(\d\d)\.(\d\d\d\d)  replace $3-$2-$1
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (1, '2013-01-01', 'Neujahr', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (3, '2013-01-06', 'Heilige Drei Könige', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (3, '2013-01-06', 'Heilige Drei Könige', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (4, '2013-03-29', 'Karfreitag', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (5, '2013-04-01', 'Ostermontag', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (6, '2013-05-01', 'Maifeiertag', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (7, '2013-05-09', 'Christi Himmelfahrt', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (8, '2013-05-20', 'Pfingstmontag', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (9, '2013-05-30', 'Fronleichnam', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (10, '2013-08-15', 'Mariä Himmelfahrt', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (11, '2013-10-03', 'Tag der Deutschen Einheit', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (12, '2013-11-01', 'Allerheiligen', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (13, '2013-12-25', '1. Weihnachtstag', 'Bayern');
INSERT INTO app.LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (14, '2013-12-26', '2. Weihnachtstag', 'Bayern');
