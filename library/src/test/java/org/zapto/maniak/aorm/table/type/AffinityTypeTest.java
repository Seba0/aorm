package org.zapto.maniak.aorm.table.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class AffinityTypeTest {

    @Parameterized.Parameters(name = "{index}: Java type: {0}; AffinityType: {1}; DateFormat: {2}")
    public static Iterable<Object[]> data() {

        final List<Object[]> data = new ArrayList<>();

        populateData(data, null);

        for (DateFormat format : DateFormat.values()) {
            populateData(data, format);
        }

        data.add(new Object[]{Date.class, AffinityType.NUMERIC, null});

        data.add(new Object[]{Date.class, AffinityType.TEXT, DateFormat.ISO8601});

        data.add(new Object[]{Date.class, AffinityType.NUMERIC, DateFormat.DATE_TIME});
        data.add(new Object[]{Date.class, AffinityType.NUMERIC, DateFormat.DATE});
        data.add(new Object[]{Date.class, AffinityType.NUMERIC, DateFormat.TIME});

        data.add(new Object[]{Date.class, AffinityType.INTEGER, DateFormat.UNIX});

        return data;
    }

    private static void populateData(List<Object[]> data, DateFormat format) {
        data.add(new Object[]{Boolean.class, AffinityType.INTEGER, format});
        data.add(new Object[]{boolean.class, AffinityType.INTEGER, format});
        data.add(new Object[]{Byte.class, AffinityType.INTEGER, format});
        data.add(new Object[]{byte.class, AffinityType.INTEGER, format});
        data.add(new Object[]{Short.class, AffinityType.INTEGER, format});
        data.add(new Object[]{short.class, AffinityType.INTEGER, format});
        data.add(new Object[]{Integer.class, AffinityType.INTEGER, format});
        data.add(new Object[]{int.class, AffinityType.INTEGER, format});
        data.add(new Object[]{Long.class, AffinityType.INTEGER, format});
        data.add(new Object[]{long.class, AffinityType.INTEGER, format});

        data.add(new Object[]{Character.class, AffinityType.TEXT, format});
        data.add(new Object[]{char.class, AffinityType.TEXT, format});
        data.add(new Object[]{String.class, AffinityType.TEXT, format});
        data.add(new Object[]{CharSequence.class, AffinityType.TEXT, format});
        data.add(new Object[]{DateFormat.class, AffinityType.TEXT, format});
        data.add(new Object[]{AffinityType.class, AffinityType.TEXT, format});

        data.add(new Object[]{Float.class, AffinityType.REAL, format});
        data.add(new Object[]{float.class, AffinityType.REAL, format});
        data.add(new Object[]{Double.class, AffinityType.REAL, format});
        data.add(new Object[]{double.class, AffinityType.REAL, format});

        data.add(new Object[]{BigDecimal.class, AffinityType.NUMERIC, format});
        data.add(new Object[]{BigInteger.class, AffinityType.NUMERIC, format});

        data.add(new Object[]{byte[].class, AffinityType.BLOB, format});

        data.add(new Object[]{null, null, format});
    }

    private final Class<?> javaType;
    private final AffinityType affinityType;
    private final DateFormat format;

    public AffinityTypeTest(Class<?> javaType, AffinityType affinityType, DateFormat format) {
        this.javaType = javaType;
        this.affinityType = affinityType;
        this.format = format;
    }

    @Test
    public void valueOf() {
        // when
        AffinityType result = AffinityType.valueOf(javaType, format);

        // then
        assertEquals(affinityType, result);
    }
}