package org.zapto.maniak.aorm.table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.zapto.maniak.aorm.annotation.Column;
import org.zapto.maniak.aorm.annotation.RowId;
import org.zapto.maniak.aorm.annotation.Table;

import java.io.Serializable;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TableUtilsTest {

    @Parameterized.Parameters(name = "{index}: name({0})={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {TestTable1.class, '"' + TestTable1.class.getName() + '"', new String[] {"_id", "seccondColumn"}},
                {TestTable2.class, "\"name_2\"", new String[] {"column_2"}},
        });
    }

    private final Class tableType;
    private final String tableName;
    private final String[] columnsNames;

    public TableUtilsTest(Class tableType, String tableName, String[] columnsNames) {
        this.tableType = tableType;
        this.tableName = tableName;
        this.columnsNames = columnsNames;
    }

    @Test
    public void testGetName() {
        // when
        String result = TableUtils.getName(tableType);

        // then
        assertEquals(tableName, result);
    }

    @Test
    public void testGetColumnsNames() {
        // when
        String[] result = TableUtils.getColumnsNames(tableType);

        // then
        assertArrayEquals(columnsNames, result);
    }

    @Table
    private class TestTable1 implements Serializable {
        @RowId
        private long firstColumn;

        @Column
        private String seccondColumn;
    }

    @Table(name = "name_2")
    private class TestTable2 implements Serializable {

        @Column(name = "column_2")
        private String firstColumn;
    }
}