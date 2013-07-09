/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.restconfig;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Matthias
 */
public class DateParameter implements Serializable {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static Date valueOf(String dateString) {
        System.out.println("convert date: " + dateString);
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException ex) {
            throw new RuntimeException("'" + dateString + "' cannot be parsed (pattern: 'yyyy-mm-dd')", ex);
        }
    }

    public static Calendar calendarValueOf(String dateString) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(valueOf(dateString));
        return calendar;
    }

    public static String valueOf(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    public static String valueOf(Calendar date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date.getTime());
    }
}
