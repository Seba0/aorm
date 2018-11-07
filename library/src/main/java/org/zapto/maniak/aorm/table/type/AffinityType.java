package org.zapto.maniak.aorm.table.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public enum AffinityType {
    TEXT,
    NUMERIC,
    INTEGER,
    REAL,
    BLOB;

    public static AffinityType valueOf(Class<?> javaType, DateFormat dateFormat) {
        if (javaType == null) {
            return null;
        } else if (javaType == Boolean.class
                || javaType == boolean.class
                || javaType == Byte.class
                || javaType == Short.class
                || javaType == short.class
                || javaType == Integer.class
                || javaType == int.class
                || javaType == Long.class
                || javaType == long.class) {
            return INTEGER;
        } else if (javaType == Character.class
                || javaType == char.class
                || javaType == String.class
                || javaType == CharSequence.class
                || javaType.isEnum()) {
            return TEXT;
        } else if (javaType == Float.class
                || javaType == float.class
                || javaType == Double.class
                || javaType == double.class) {
            return REAL;
        } else if (javaType == Date.class) {
            if (dateFormat == DateFormat.UNIX) {
                return INTEGER;
            } else if (dateFormat == DateFormat.ISO8601) {
                return TEXT;
            }
            return NUMERIC;
        } else if (javaType == BigDecimal.class
                || javaType == BigInteger.class) {
            return NUMERIC;
        } else if (javaType == byte.class
                || javaType == byte[].class) {
            return javaType.isArray()
                    ? BLOB
                    : INTEGER;
        }
        return null;
    }
}
