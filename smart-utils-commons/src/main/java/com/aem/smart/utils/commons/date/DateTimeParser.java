package com.aem.smart.utils.commons.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

/**
 * The class which parse date time as String to Calendar.
 */
public final class DateTimeParser {

    private static final Logger LOG = LoggerFactory.getLogger(DateTimeParser.class);

    private static final String INVALID_FORMAT = "INVALID FORMAT";

    private static final Set<String> FORMATS = ImmutableSet.<String>builder().add("yyyy-MM-dd'T'HH:mm:ssX")
            .add("yyyy-MM-dd").add("yyyy-MM-dd'T'HH:mm:ss").add("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .add("yyyy-MM-dd'T'HH:mm:ss.SSSX").add("yyyy-MM-dd'T'HH:mm:ss'Z'").add("yyyy-MM-dd'T'HH:mm:ssZ")
            .add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").add("yyyy-MM-dd'T'HH:mm:ss.SSS").add("yyyy-MM-dd HH:mm:ss")
            .add("MM/dd/yyyy HH:mm:ss").add("MM/dd/yyyy'T'HH:mm:ss.SSS'Z'").add("MM/dd/yyyy'T'HH:mm:ss.SSSZ")
            .add("MM/dd/yyyy'T'HH:mm:ss.SSS").add("MM/dd/yyyy'T'HH:mm:ssZ").add("MM/dd/yyyy'T'HH:mm:ss")
            .add("yyyy:MM:dd HH:mm:ss").add("yyyyMMdd").add("yyyy/MM/dd").build();

    /**
     * Parse datetime from String to Date by pattern.
     *
     * @param value the value that represent datetime.
     *
     * @return the optional
     */
    public static Date toDate(final String value, final String pattern) {
        Date date = null;

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            date = formatter.parse(value);
        } catch (ParseException e) {
            LOG.error("Error while parsing '{}' with pattern '{}'", new Object[] { value, pattern, e, });
            Throwables.propagate(e);
        }
        return date;
    }

    /**
     * Parse datetime from String to Calendar.
     *
     * @param value the value that represent datetime.
     *
     * @return the optional
     */
    public static Optional<Calendar> parse(final String value) throws ParseException {
        Optional<Calendar> parsedDate = parse(value, null);
        if (parsedDate.isPresent()) {
            return parsedDate;
        } else {
            throw new ParseException(INVALID_FORMAT, 0);
        }
    }

    /**
     * Parse datetime from String to Calendar.
     *
     * @param value        the value that represent datetime.
     * @param defaultValue the default value
     * @return the optional
     */
    public static Optional<Calendar> parse(final String value, final Calendar defaultValue) {
        Calendar result = defaultValue;
        if (null != value) {
            for (final String format : FORMATS) {
                try {
                    final DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
                    result = formatter.parseDateTime(value).toGregorianCalendar();
                    break;
                } catch (final IllegalArgumentException ex) {
                    LOG.trace("Fail to parse date time for value '{}'", value, ex);
                }
            }
            if (result == null) {
                LOG.error("Fail to parse date time at all: '{}' for any supported pattern", value);
            }
        }
        return Optional.fromNullable(result);
    }

    private DateTimeParser() {
    }
}
