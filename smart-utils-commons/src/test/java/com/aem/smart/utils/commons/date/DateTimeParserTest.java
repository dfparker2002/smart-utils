package com.aem.smart.utils.commons.date;

import com.google.common.base.Optional;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;

import static org.junit.Assert.*;

public class DateTimeParserTest {

    @Test
    public void parseDateTimeSecondsWithTimeZone() throws ParseException {
        String value = "2011-02-14T10:10:00.000+02:00";
        final Optional<Calendar> parse = DateTimeParser.parse(value);

        final Calendar calendar = parse.get();

        assertEquals("Expect 2011", 2011, calendar.get(Calendar.YEAR));
        assertEquals("Expect 1", 1, calendar.get(Calendar.MONTH));
        assertEquals("Expect 14", 14, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals("Expect 10", 10, calendar.get(Calendar.HOUR));
        assertEquals("Expect 10", 10, calendar.get(Calendar.MINUTE));
    }

    @Test
    public void parseDateTimeSeconds() throws ParseException {
        String value = "2001-10-01T01:22:22.000";
        final Optional<Calendar> parse = DateTimeParser.parse(value);

        final Calendar calendar = parse.get();

        assertEquals("Expect 2001", 2001, calendar.get(Calendar.YEAR));
        assertEquals("Expect 9", 9, calendar.get(Calendar.MONTH));
        assertEquals("Expect 01", 1, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals("Expect 1", 1, calendar.get(Calendar.HOUR));
        assertEquals("Expect 22", 22, calendar.get(Calendar.MINUTE));
    }

    @Test
    public void parseDateTime() throws ParseException {
        String value = "2016-08-14T08:08:00";
        final Optional<Calendar> parse = DateTimeParser.parse(value);

        final Calendar calendar = parse.get();

        assertEquals("Expect 2016", 2016, calendar.get(Calendar.YEAR));
        assertEquals("Expect 7", 7, calendar.get(Calendar.MONTH));
        assertEquals("Expect 14", 14, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals("Expect 8", 8, calendar.get(Calendar.HOUR));
        assertEquals("Expect 8", 8, calendar.get(Calendar.MINUTE));
    }

    @Test
    public void parseDateTimeWithWhitespace() throws ParseException {
        String value = "2099-09-20 01:07:00";
        final Optional<Calendar> parse = DateTimeParser.parse(value);

        final Calendar calendar = parse.get();

        assertEquals("Expect 2099", 2099, calendar.get(Calendar.YEAR));
        assertEquals("Expect 8", 8, calendar.get(Calendar.MONTH));
        assertEquals("Expect 20", 20, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals("Expect 1", 1, calendar.get(Calendar.HOUR));
        assertEquals("Expect 7", 7, calendar.get(Calendar.MINUTE));
    }

    @Test
    public void parseDate() throws ParseException {
        String value = "2098-12-25";
        final Optional<Calendar> parse = DateTimeParser.parse(value);

        final Calendar calendar = parse.get();

        assertEquals("Expect 2098", 2098, calendar.get(Calendar.YEAR));
        assertEquals("Expect 11", 11, calendar.get(Calendar.MONTH));
        assertEquals("Expect 25", 25, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test(expected = ParseException.class)
    public void parseNotValidString() throws ParseException {
        String value = "bklsdjfk016sad-02-14";
        DateTimeParser.parse(value);
    }


}