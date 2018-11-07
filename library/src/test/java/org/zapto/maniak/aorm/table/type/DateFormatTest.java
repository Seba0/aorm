package org.zapto.maniak.aorm.table.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DateFormatTest {

    private final DateFormat format;
    private final Date date;
    private final String formatedDate;

    private static Date parseDate(String date, String format) throws ParseException {
        return new SimpleDateFormat(format, Locale.ROOT).parse(date);
    }

    private static long getUnix() {
        return System.currentTimeMillis() / 1000;
    }

    @Parameterized.Parameters(name = "{index}: name({0})={1} : {2}")
    public static Iterable<Object[]> data() throws ParseException {

        final String iso8601 = "2001-07-04T12:08:56.235+0200";
        final String date = "20181106";
        final String time = "174000";
        final String dateTime = date + time;
        final long unixDate = getUnix();

        final String iso8601Format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        final String dateFormat = "yyyyMMdd";
        final String timeFormat = "HHmmss";
        final String dateTimeFormat = dateFormat + timeFormat;

        return Arrays.asList(new Object[][]{
                {DateFormat.ISO8601, parseDate(iso8601, iso8601Format), iso8601},
                {DateFormat.DATE_TIME, parseDate(dateTime, dateTimeFormat), dateTime},
                {DateFormat.DATE, parseDate(date, dateFormat), date},
                {DateFormat.TIME, parseDate(time, timeFormat), time},
                {DateFormat.UNIX, new Date(unixDate * 1000), Long.toString(unixDate)},
                {DateFormat.ISO8601, null, null},
                {DateFormat.DATE_TIME, null, null},
                {DateFormat.DATE, null, null},
                {DateFormat.TIME, null, null},
                {DateFormat.UNIX, null, null},
        });
    }

    public DateFormatTest(DateFormat format, Date date, String formatedDate) {
        this.format = format;
        this.date = date;
        this.formatedDate = formatedDate;
    }

    @Test
    public void format() {
        // when
        String result = format.format(date);

        // then
        assertEquals(formatedDate, result);
    }

    @Test
    public void parse() throws ParseException {

        // when
        Date result = format.parse(formatedDate);

        // then
        assertEquals(date, result);
    }
}