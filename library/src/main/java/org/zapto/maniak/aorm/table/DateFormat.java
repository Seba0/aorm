package org.zapto.maniak.aorm.table;

import java.text.*;
import java.util.*;

/**
 *
 * @author Seba_0
 */
public enum DateFormat {
    DATE_TIME("yyyyMMddHHmmss"),
    DATE("yyyyMMdd"),
    TIME("HHmmss"),
    UNIX;

    DateFormat(String format) {
        this.format = new SimpleDateFormat(format);
    }

    DateFormat() {
        this.format = null;
    }

    private final java.text.DateFormat format;

    String format(Date date) {
        if (date == null) {
            return null;
        }
        if (format == null) {
            return Long.toString(date.getTime() / 1000);
        }
        return format.format(date);
    }

    Date parse(String text) throws ParseException {
        if (text == null || text.isEmpty()) {
            return null;
        }
        if (format == null) {
            return new Date(Long.parseLong(text) * 1000);
        }
        return format.parse(text);
    }
}
