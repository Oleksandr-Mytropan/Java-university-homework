package cz.upce.boop.ex.collection;

import java.util.Iterator;

public interface BIterator<T> extends Iterator<T> {

    boolean hasNext();

    T next();

    default void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
