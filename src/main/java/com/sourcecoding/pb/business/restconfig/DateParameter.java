/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.restconfig;

import static com.sourcecoding.pb.business.restconfig.DateParameter.valueOf;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author Matthias
 */
public class DateParameter implements Serializable {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();
    private final static DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.date();
    
    public static Date valueOf(String dateString) {
        if (dateString == null || dateString.isEmpty())
            return null;
        if (dateString.length() > 12) {
            return DATE_TIME_FORMATTER.parseDateTime(dateString).toDate();
        }
        return DATE_FORMATTER.parseDateTime(dateString).toDate();
    }

    public static Calendar calendarValueOf(String dateString) {
        Calendar result = new GregorianCalendar();
        result.setTimeInMillis( valueOf(dateString).getTime() );
        return result;
    }

    public static String valueOf(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMATTER.print(new DateTime(date.getTime()));
    }

    public static String valueOf(Calendar date) {
        return valueOf(date.getTime());
    }
    
    public static String valueOfDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_TIME_FORMATTER.print(new DateTime(date.getTime()));
    }

    public static String valueOfDateTime(Calendar date) {
        return valueOfDateTime(date.getTime());
    }
}
