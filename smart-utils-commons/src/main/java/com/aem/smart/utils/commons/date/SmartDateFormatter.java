package com.aem.smart.utils.commons.date;

import java.util.Calendar;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * The type Date formatter.
 */
public final class SmartDateFormatter {

    private SmartDateFormatter() {
    }

    /**
     * Format Calendar by provided pattern and return empty string if null value or wrong pattern.
     *
     * @param date    the date
     * @param pattern the pattern
     * @return the string
     */
    public static String format(final Calendar date, final String pattern) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
        return date == null ? "" : dtf.print(date.getTimeInMillis());
    }
}
