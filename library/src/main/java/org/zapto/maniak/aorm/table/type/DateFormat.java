package org.zapto.maniak.aorm.table.type;

import java.text.*;
import java.util.*;

/**
 * @author Seba_0
 */
public enum DateFormat {
    ISO8601("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
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

    public String format(Date date) {
        if (date == null) {
            return null;
        }
        if (format == null) {
            return Long.toString(date.getTime() / 1000);
        }
        return format.format(date);
    }

    public Date parse(String text) throws ParseException {
        if (text == null || text.isEmpty()) {
            return null;
        }
        if (format == null) {
            return new Date(Long.parseLong(text) * 1000);
        }
        return format.parse(text);
    }
}
