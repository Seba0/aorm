package org.zapto.maniak.aorm.table;

import android.database.*;
import android.database.sqlite.*;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.util.*;

import org.zapto.maniak.aorm.annotation.*;
import org.zapto.maniak.aorm.table.type.AffinityType;
import org.zapto.maniak.aorm.table.type.Collate;
import org.zapto.maniak.aorm.table.type.DateFormat;

import android.content.*;
import android.provider.BaseColumns;


/**
 * @author Seba_0
 */
public final class TableUtils {

    public static final String DEFAULT = "__default__";
    static final String AORM_TABLE = TableHelper.NAME + "_metadata";

    public static <T extends Serializable> String getName(Class<T> table) {
        if (table.isAnnotationPresent(Table.class)) {
            Table t = table.getAnnotation(Table.class);
            if (!DEFAULT.equals(t.name())) {
                return "\"" + t.name() + "\"";
            }
        }
        return "\"" + table.getName() + "\"";
    }

    public static String getColumnName(Field column) {
        if (column.isAnnotationPresent(RowId.class)) {
            return BaseColumns._ID;
        }
        Column c = column.getAnnotation(Column.class);
        if (c != null && !DEFAULT.equals(c.name())) {
            return c.name();
        }
        return column.getName();
    }

    public static <T extends Serializable> String[] getColumnsNames(Class<T> table) {
        List<String> names = new ArrayList<>();
        for (Field f : table.getDeclaredFields()) {
            if (f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(RowId.class)) {
                names.add(getColumnName(f));
            }
        }
        return names.toArray(new String[0]);
    }

    public static <T extends Serializable> ContentValues objectToRow(T object) throws IllegalAccessException {
        ContentValues cv = new ContentValues();
        Class<? extends Serializable> table = object.getClass();
        for (Field f : table.getDeclaredFields()) {
            f.setAccessible(true);
            Object value = f.get(object);
            if (value == null) {
                continue;
            }
            RowId rowId = f.getAnnotation(RowId.class);
            if (rowId != null) {
                cv.put(BaseColumns._ID, (Long) value);
                continue;
            }
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            String cname = TableUtils.getColumnName(f);
            Class<?> type = f.getType();
            if (type != byte.class && type.isArray()) {
                throw new UnsupportedOperationException("Field type '" + type + "' is not supported.");
            } else if (type == Boolean.class || type == boolean.class) {
                cv.put(cname, (Boolean) value);
            } else if (type == Byte.class || (type == byte.class && !type.isArray())) {
                cv.put(cname, (Byte) value);
            } else if (type == Short.class || type == short.class) {
                cv.put(cname, (Short) value);
            } else if (type == Integer.class || type == int.class) {
                cv.put(cname, (Integer) value);
            } else if (type == Long.class || type == long.class) {
                cv.put(cname, (Long) value);
            } else if (type == Character.class || type == char.class) {
                cv.put(cname, String.valueOf(value));
            } else if (type == String.class) {
                cv.put(cname, (String) value);
            } else if (type == CharSequence.class) {
                CharSequence s = (CharSequence) value;
                cv.put(cname, s.toString());
            } else if (type == Float.class || type == float.class) {
                cv.put(cname, (Float) value);
            } else if (type == Double.class || type == double.class) {
                cv.put(cname, (Double) value);
            } else if (type == Date.class) {
                DateFormat df = column.dateFormat();
                String format = df.format((Date) value);
                if (df == DateFormat.UNIX) {
                    cv.put(cname, Long.parseLong(format));
                } else {
                    cv.put(cname, format);
                }
            } else if (type == BigDecimal.class) {
                BigDecimal b = (BigDecimal) value;
                cv.put(cname, b.toPlainString());
            } else if (type == BigInteger.class) {
                BigInteger b = (BigInteger) value;
                cv.put(cname, b.toString());
            } else if (type.isEnum()) {
                Enum e = (Enum) value;
                cv.put(cname, e.name());
            } else if (type == byte.class) {
                cv.put(cname, (byte[]) value);
            } else {
                throw new UnsupportedOperationException("Field type '" + type + "' is not supported.");
            }
        }
        return cv;
    }

    public static <T extends Serializable> T rowToObject(Class<T> table, Cursor c) throws Exception {
        T e = table.newInstance();
        for (Field f : table.getDeclaredFields()) {
            f.setAccessible(true);
            Column col = f.getAnnotation(Column.class);
            if (col == null) {
                continue;
            }
            String name = TableUtils.getColumnName(f);
            int index = c.getColumnIndexOrThrow(name);
            Class<?> type = f.getType();
            if (type != byte.class && type.isArray()) {
                continue;
            } else if (c.isNull(index)) {
                f.set(e, null);
            } else if (type == Boolean.class || type == boolean.class) {
                f.setBoolean(e, c.getInt(index) > 0);
            } else if (type == Byte.class || type == byte.class) {
                f.setByte(e, (byte) c.getShort(index));
            } else if (type == Short.class || type == short.class) {
                f.setShort(e, c.getShort(index));
            } else if (type == Integer.class || type == int.class) {
                f.setInt(e, c.getInt(index));
            } else if (type == Long.class || type == long.class) {
                f.setLong(e, c.getLong(index));
            } else if (type == Character.class || type == char.class) {
                String string = c.getString(index);
                if (!string.isEmpty()) {
                    f.setChar(e, string.charAt(0));
                }
            } else if (type == String.class
                    || type == CharSequence.class) {
                f.set(e, c.getString(index));
            } else if (type == Float.class || type == float.class) {
                f.setFloat(e, c.getFloat(index));
            } else if (type == Double.class || type == double.class) {
                f.setDouble(e, c.getDouble(index));
            } else if (type == Date.class) {
                f.set(e, col.dateFormat().parse(c.getString(index)));
            } else if (type == BigDecimal.class) {
                f.set(e, new BigDecimal(c.getString(index)));
            } else if (type == BigInteger.class) {
                f.set(e, new BigInteger(c.getString(index)));
            } else if (type.isEnum()) {
                Object o = Enum.valueOf((Class<Enum>) type, c.getString(index));
                f.set(e, o);
            } else if (type == byte[].class) {
                f.set(e, c.getBlob(index));
            }
        }
        return e;
    }

    public static <T extends Serializable> int getTableVersion(Class<T> table, SQLiteDatabase db) {
        Cursor c = db.query(AORM_TABLE, new String[]{"VERSION"}, "NAME = ?", new String[]{getName(table)}, null, null, null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        }
        return -1;
    }

    public static <T extends Serializable> int createTable(Class<T> table, SQLiteDatabase db) {
        Table t = table.getAnnotation(Table.class);
        if (t == null) {
            throw new UnsupportedOperationException("Class '" + table + "' is not supported.");
        }
        int i = getTableVersion(table, db);
        if (db.isReadOnly() || i >= 0) {
            return i;
        }
        String name = getName(table);
        if (AORM_TABLE.equals(name)) {
            throw new UnsupportedOperationException("Table name '" + name + "' is not allowed");
        }
        StringBuilder sql = new StringBuilder("CREATE TABLE ")
                .append(name).append("(");
        for (Field f : table.getDeclaredFields()) {
            Class<?> type = f.getType();
            if (type != byte.class && type.isArray()) {
                throw new UnsupportedOperationException("Unsupported array of type '" + type + "'");
            }
            RowId rowId = f.getAnnotation(RowId.class);
            if (rowId != null) {
                if (type == Long.class
                        || type == long.class) {
                    sql.append(BaseColumns._ID)
                            .append(' ')
                            .append(AffinityType.INTEGER)
                            .append(" PRIMARY KEY,");
                    continue;
                }
                throw new UnsupportedOperationException("Unsupported ROWID type '" + type + "'");
            }
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            String cname = getColumnName(f);
            sql.append(cname);
            AffinityType affinityType = AffinityType.valueOf(type, column.dateFormat());
            if (affinityType == null) {
                throw new UnsupportedOperationException("Field type '" + type + "' is not supported.");
            }
            sql.append(' ')
                    .append(affinityType);
            if (column.unique()) {
                sql.append(" UNIQUE");
            }
            PrimaryKey primaryKey = f.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                sql.append(" PRIMARY KEY");
                if (primaryKey.autoincrement()) {
                    sql.append(" AUTOINCREMENT");
                }
            }
            if (type.isPrimitive() || !column.nullable()) {
                sql.append(" NOT NULL");
            }
            if (column.collate() != Collate.NOT_SET) {
                sql.append(" COLLATE ").append(column.collate().name());
            }
            sql.append(',');
        }
        sql.deleteCharAt(sql.length() - 1).append(')');
        db.execSQL(sql.toString());
        List<Index> ids = new ArrayList<>(Arrays.asList(table.getAnnotationsByType(Index.class)));
        ids.addAll(Arrays.asList(t.indexes()));
        for (Index idx : ids) {
            String isql = createIndex(name, idx);
            db.execSQL(isql);
        }
        setTableVersion(table, db);
        return t.version();
    }

    private static <T extends Serializable> String createIndex(String tname, Index i) {
        StringBuilder sb = new StringBuilder("CREATE ");
        if (i.unique()) {
            sb.append("UNIQUE ");
        }
        sb.append("INDEX ");
        String[] s = i.columns();
        String name = i.name();
        if (TableUtils.DEFAULT.equals(name)) {
            if (i.unique()) {
                sb.append("UNI");
            } else {
                sb.append("IDX");
            }
            sb.append('_')
                    .append(Integer.toHexString(Arrays.hashCode(s)));
        } else {
            sb.append(name);
        }
        sb.append(" ON ")
                .append(tname)
                .append(" (");
        boolean b = false;
        for (String col : s) {
            if (b) {
                sb.append(", ");
            }
            sb.append(col);
            b = true;
        }
        sb.append(")");
        return sb.toString();
    }

    private static <T extends Serializable> void setTableVersion(Class<T> table, SQLiteDatabase db) {
        String name = getName(table);
        ContentValues cv = new ContentValues(1);
        Table t = table.getAnnotation(Table.class);
        if (t != null) {
            cv.put("VERSION", t.version());
        }
        if (db.update(AORM_TABLE, cv, "NAME = ?", new String[]{name}) < 1) {
            cv.put("NAME", name);
            db.insertOrThrow(AORM_TABLE, null, cv);
        }
    }
}
