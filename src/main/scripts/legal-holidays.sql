-- legal holidays are from http://www.feiertage.net/frei-tage.php
-- regex (notepad++) search (\d\d)\.(\d\d)\.(\d\d\d\d)  replace $3-$2-$1

DELETE FROM LEGALHOLIDAY;
-- regex (notepad++) search (\d\d)\.(\d\d)\.(\d\d\d\d)  replace $3-$2-$1
-- regex (notepad++) search (\d\d)\.(\d\d)\.(\d\d\d\d)  replace $3-$2-$1
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (1, '2013-01-01', 'Neujahr', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (2, '2013-01-06', 'Heilige Drei Könige', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (3, '2013-01-06', 'Heilige Drei Könige', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (4, '2013-03-29', 'Karfreitag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (5, '2013-04-01', 'Ostermontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (6, '2013-05-01', 'Maifeiertag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (7, '2013-05-09', 'Christi Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (8, '2013-05-20', 'Pfingstmontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (9, '2013-05-30', 'Fronleichnam', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (10, '2013-08-15', 'Mariä Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (11, '2013-10-03', 'Tag der Deutschen Einheit', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (12, '2013-11-01', 'Allerheiligen', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (13, '2013-12-25', '1. Weihnachtstag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (14, '2013-12-26', '2. Weihnachtstag', 'Bayern');

INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (20, '2014-01-01', 'Neujahr', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (21, '2014-01-06', 'Heilige Drei Könige', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (22, '2014-04-18', 'Karfreitag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (23, '2014-04-21', 'Ostermontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (24, '2014-05-01', 'Maifeiertag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (25, '2014-05-29', 'Christi Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (26, '2014-06-09', 'Pfingstmontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (27, '2014-06-19', 'Fronleichnam', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (28, '2014-08-15', 'Mariä Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (29, '2014-10-03', 'Tag der Deutschen Einheit', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (30, '2014-11-01', 'Allerheiligen', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (31, '2014-12-25', '1. Weihnachtstag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (32, '2014-12-26', '2. Weihnachtstag', 'Bayern');


INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (33, '2015-01-01', 'Neujahr', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (34, '2015-01-06', 'Heilige Drei Könige', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (35, '2015-04-03', 'Karfreitag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (36, '2015-04-06', 'Ostermontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (37, '2015-05-01', 'Maifeiertag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (38, '2015-05-14', 'Christi Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (39, '2015-05-25', 'Pfingstmontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (40, '2015-06-04', 'Fronleichnam', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (41, '2015-08-15', 'Mariä Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (42, '2015-10-03', 'Tag der Deutschen Einheit', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (43, '2015-11-01', 'Allerheiligen', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (44, '2015-12-25', '1. Weihnachtstag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (45, '2015-12-26', '2. Weihnachtstag', 'Bayern');

INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (50, '2016-01-01', 'Neujahr', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (51, '2016-01-06', 'Heilige Drei Könige', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (52, '2016-25-03', 'Karfreitag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (53, '2016-28-03', 'Ostermontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (54, '2016-05-01', 'Maifeiertag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (55, '2016-05-05', 'Christi Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (56, '2016-05-16', 'Pfingstmontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (57, '2016-05-26', 'Fronleichnam', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (58, '2016-08-15', 'Mariä Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (59, '2016-10-03', 'Tag der Deutschen Einheit', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (60, '2016-11-01', 'Allerheiligen', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (61, '2016-12-25', '1. Weihnachtstag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (62, '2016-12-26', '2. Weihnachtstag', 'Bayern');


INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (70, '2017-01-01', 'Neujahr', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (71, '2017-01-06', 'Heilige Drei Könige', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (72, '2017-04-14', 'Karfreitag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (73, '2017-04-17', 'Ostermontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (74, '2017-05-01', 'Maifeiertag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (75, '2017-05-25', 'Christi Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (76, '2017-06-05', 'Pfingstmontag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (77, '2017-06-15', 'Fronleichnam', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (78, '2017-08-15', 'Mariä Himmelfahrt', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (79, '2017-10-03', 'Tag der Deutschen Einheit', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (80, '2017-11-01', 'Allerheiligen', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (81, '2017-12-25', '1. Weihnachtstag', 'Bayern');
INSERT INTO LEGALHOLIDAY(ID, legalholidaydate, legalholidyname, legalholidaystate) VALUES (82, '2017-12-26', '2. Weihnachtstag', 'Bayern');


COMMIT;