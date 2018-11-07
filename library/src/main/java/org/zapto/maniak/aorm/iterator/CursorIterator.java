package org.zapto.maniak.aorm.iterator;

import android.database.*;

import java.io.*;
import java.util.*;

import org.zapto.maniak.aorm.table.*;

public class CursorIterator<E extends Serializable> implements ResultIterator<E> {
    private final Cursor cursor;
    private final Closeable closeable;
    private final Class<E> table;

    public CursorIterator(Cursor cursor, Class<E> table, Closeable closeable) {
        this.cursor = cursor;
        this.closeable = closeable;
        this.table = table;
    }

    public boolean hasNext() {
        int count = cursor.getCount();
        int position = cursor.getPosition();
        return count > position + 1;
    }

    public E next() {
        if (cursor.moveToNext()) {
            try {
                return TableUtils.rowToObject(table, cursor);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        cursor.close();
        closeable.close();
    }
}
