package org.zapto.maniak.aorm.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 *
 * @author Seba_0
 */
public final class EmptyIterator<E> implements Iterator<E> {

    public boolean hasNext() {
        return false;
    }

    public E next() {
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
