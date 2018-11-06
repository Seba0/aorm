package org.zapto.maniak.aorm.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.zapto.maniak.aorm.iterator.EmptyIterator;
import org.zapto.maniak.aorm.annotation.RowId;
import android.content.*;
import android.provider.*;
import java.io.File;
import org.zapto.maniak.aorm.iterator.*;

/**
 *
 * @author Seba_0
 */
public final class TableHelper extends SQLiteOpenHelper {

    public final static int VERSION = 1;
    public final static String NAME = "aorm";
    public final Context context;

    public TableHelper(Context context) {
        super(context.getApplicationContext(), NAME, null, VERSION);
        this.context = context;
    }
    public TableHelper(Context context, String name) {
        super(context.getApplicationContext(), name, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 0:
                db.execSQL("CREATE TABLE " + TableUtils.AORM_TABLE + "(NAME TEXT UNIQUE PRIMARY KEY NOT NULL, VERSION INTEGER NOT NULL DEFAULT 0)");
        }
    }

    public <E extends Serializable> ResultIterator<E> query(final Class<E> table, String selection, String... selectionArgs) {
        return query(table, selection, selectionArgs, null, null, null);
    }

    public <E extends Serializable> ResultIterator<E> query(final Class<E> table, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase rdb = getReadableDatabase();
        int v = TableUtils.getTableVersion(table, rdb);
        if (v < 0) {
            return new EmptyIterator<E>();
        }
        String name = TableUtils.getName(table);
        String[] columns = TableUtils.getColumnsNames(table);
        final Cursor c = rdb.query(name, columns, selection, selectionArgs, groupBy, having, orderBy);
        return new CursorIterator<E>(c, table, rdb);
    }

    public <E extends Serializable> int update(E object) throws Exception {
        try(SQLiteDatabase wdb = getWritableDatabase()) {
			Class<? extends Serializable> table = object.getClass();
			int v = TableUtils.getTableVersion(table, wdb);
			if (v < 0) {
				return -1;
			}
			ContentValues cv = TableUtils.objectToRow(object);
			String id = cv.getAsString(BaseColumns._ID);
			cv.remove(BaseColumns._ID);
			int i = wdb.update(TableUtils.getName(table), cv, BaseColumns._ID + " = ?", new String[]{id});
			return i;
		}
    }

    public <E extends Serializable> long insert(E object) throws Exception {
        try(SQLiteDatabase wdb = getWritableDatabase()) {
			Class<? extends Serializable> table = object.getClass();
			int v = TableUtils.getTableVersion(table, wdb);
			if (v < 0) {
				TableUtils.createTable(table, wdb);
			}
			ContentValues cv = TableUtils.objectToRow(object);
			cv.remove(BaseColumns._ID);
			long id = wdb.insertOrThrow(TableUtils.getName(table), null, cv);
			for (Field f : table.getDeclaredFields()) {
				if (f.isAnnotationPresent(RowId.class)) {
					f.setAccessible(true);
					f.setLong(object, id);
				}
			}
			return id;
		}
    }

    public <E extends Serializable> int insert(Iterable<E> objects) throws Exception {
        try(SQLiteDatabase wdb = getWritableDatabase()) {
			Class<? extends Serializable> table = null;
			String name = null;
			int i = 0;
			for (E e : objects) {
				if (table == null) {
					table = e.getClass();
					int v = TableUtils.getTableVersion(table, wdb);
					if (v < 0) {
						TableUtils.createTable(table, wdb);
					}
				}
				if (name == null) {
					name = TableUtils.getName(table);
				}
				ContentValues cv = TableUtils.objectToRow(e);
				cv.remove(BaseColumns._ID);
				long id = wdb.insert(name, null, cv);
				for (Field f : table.getDeclaredFields()) {
					if (f.isAnnotationPresent(RowId.class)) {
						f.setAccessible(true);
						f.setLong(e, id);
					}
				}
				if (id >= 0) {
					i++;
				}
			}
			return i;
		}
    }

    public void backup() {
        File databasePath = context.getDatabasePath(getDatabaseName());

    }
}
