package org.zapto.maniak.aorm.table;

import android.database.*;
import android.database.sqlite.*;
import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.util.*;
import org.zapto.maniak.aorm.annotation.*;
import android.content.*;
import android.provider.BaseColumns;


/**
 *
 * @author Seba_0
 */
public class TableUtils {

    public static final String DEFAULT = "__default__";
    static final String AORM_TABLE = TableHelper.NAME + "_metadata";

    static <T extends Serializable> String getName(Class<T> table) {
        if (table.isAnnotationPresent(Table.class)) {
            Table t = table.getAnnotation(Table.class);
            if (!DEFAULT.equals(t.name())) {
                return t.name();
            }
        }
        return table.getName();
    }

    static String getColumnName(Field column) {
        if (column.isAnnotationPresent(Column.class)) {
            Column c = column.getAnnotation(Column.class);
            if (!DEFAULT.equals(c.name())) {
                return c.name();
            }
        }
        return column.getName();
    }

    static <T extends Serializable> String[] getColumnsNames(Class<T> table) {
        List<String> names = new ArrayList<String>();
        for (Field f : table.getDeclaredFields()) {
            if (f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(RowId.class)) {
                names.add(getColumnName(f));
            }
        }
        return names.toArray(new String[0]);
    }

    static <T extends Serializable> ContentValues objectToRow(T object) throws IllegalAccessException {
        ContentValues cv = new ContentValues();
        Class<? extends Serializable> table = object.getClass();
        String name = getName(table);
        for (Field f : table.getDeclaredFields()) {
            f.setAccessible(true);
            RowId rowId = f.getAnnotation(RowId.class);
            if (rowId != null) {
                try {
                    cv.put(BaseColumns._ID, f.getLong(object));
                } catch (NullPointerException e) {
                }
                continue;
            }
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            try {
                String cname = TableUtils.getColumnName(f);
                Class<?> type = f.getType();
                if (type != byte.class && type.isArray()) {
                } else if (type == Boolean.class || type == boolean.class) {
                    cv.put(cname, f.getBoolean(object));
                } else if (type == Byte.class || (type == byte.class && !type.isArray())) {
                    cv.put(cname, f.getByte(object));
                } else if (type == Short.class || type == short.class) {
                    cv.put(cname, f.getShort(object));
                } else if (type == Integer.class || type == int.class) {
                    cv.put(cname, f.getInt(object));
                } else if (type == Long.class || type == long.class) {
                    cv.put(cname, f.getLong(object));
                } else if (type == Character.class || type == char.class) {
                    cv.put(cname, Character.toString(f.getChar(object)));
                } else if (type == String.class) {
                    String s = (String) f.get(object);
                    if (s != null) {
                        cv.put(cname, s);
                    }
                } else if (type == CharSequence.class) {
                    CharSequence s = (CharSequence) f.get(object);
                    if (s != null) {
                        cv.put(cname, s.toString());
                    }
                } else if (type == Float.class || type == float.class) {
                    cv.put(cname, f.getFloat(object));
                } else if (type == Double.class || type == double.class) {
                    cv.put(cname, f.getDouble(object));
                } else if (type == Date.class) {
                    Date d = (Date) f.get(object);
                    if (d != null) {
                        DateFormat df = column.dateFormat();
                        String format = df.format(d);
                        if (df == DateFormat.UNIX) {
                            cv.put(cname, Long.parseLong(format));
                        } else {
                            cv.put(cname, format);
                        }
                    }
                } else if (type == BigDecimal.class) {
                    BigDecimal b = (BigDecimal) f.get(object);
                    cv.put(name, b.toPlainString());
                } else if (type == BigInteger.class) {
                    BigInteger b = (BigInteger) f.get(object);
                    cv.put(name, b.toString());
                } else if (type == BigInteger.class) {
                    BigInteger b = (BigInteger) f.get(object);
                    cv.put(name, b.toString());
                } else if (type == byte.class) {
                    byte[] b = (byte[]) f.get(object);
                    cv.put(name, b);
                } else {
                    throw new UnsupportedOperationException("Field type '" + type + "' is not supported.");
                }
            } catch (NullPointerException e) {
            }
        }
        return cv;
    }

    static <T extends Serializable> T rowToObject(Class<T> table, Cursor c) throws Exception {
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
//            } else if (type.isEnum()) {
//                f.set(e, c.getBlob(index))
            } else if (type == byte[].class) {
                f.set(e, c.getBlob(index));
            }
        }
        return e;
    }

    static <T extends Serializable> int getTableVersion(Class<T> table, SQLiteDatabase db) {
        Cursor c = db.query(AORM_TABLE, new String[]{"VERSION"}, "NAME = ?", new String[]{getName(table)}, null, null, null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        }
        return -1;
    }

    static <T extends Serializable> int createTable(Class<T> table, SQLiteDatabase db) {
        Table t = table.getAnnotation(Table.class
        );
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
                .append(name).append('(');
        for (Field f : table.getDeclaredFields()) {
            RowId rowId = f.getAnnotation(RowId.class);
            String cname = getColumnName(f);
            Class<?> type = f.getType();
            if (type != byte.class && type.isArray()) {
                throw new UnsupportedOperationException("Unsupported array of type '" + type + "'");
            }
            if (rowId != null) {
                if (type == Long.class
                        || type == long.class) {
                    sql.append(BaseColumns._ID).append(" INTEGER PRIMARY KEY,");
                    continue;
                }
                throw new UnsupportedOperationException("Unsupported ROWID type '" + type + "'");
            }
            Column column = f.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            sql.append(cname);
            if (type == Boolean.class
                    || type == boolean.class
                    || type == Byte.class
                    || type == Short.class
                    || type == short.class
                    || type == Integer.class
                    || type == int.class
                    || type == Long.class
                    || type == long.class) {
                sql.append(" INTEGER");
            } else if (type == Character.class
                    || type == char.class
                    || type == String.class
                    || type == CharSequence.class) {
                sql.append(" TEXT");
            } else if (type == Float.class
                    || type == float.class
                    || type == Double.class
                    || type == double.class) {
                sql.append(" REAL");
            } else if (type == Date.class) {
                DateFormat df = DateFormat.UNIX;
                Column col = type.getAnnotation(Column.class);
                if (col != null) {
                    df = col.dateFormat();
                }
                sql.append(df == DateFormat.UNIX ? " INTEGER" : " NUMERIC");
            } else if (type == BigDecimal.class
                    || type == BigInteger.class) {
                sql.append(" NUMERIC");
            } else if (type == byte.class) {
                sql.append(type.isArray() ? " BLOB" : " INTEGER");
            } else {
                throw new UnsupportedOperationException("Field type '" + type + "' is not supported.");
            }
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
        setTableVersion(table, db);
        return t.version();
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
