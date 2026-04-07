package cz.upce.boop.ex.collection;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface BList<T> extends BIterable<T> {

    void add(T o);

    int size();

    T get(int i);

    T set(int index, T element);

    T remove(int index);

    BIterator<T> iterator();

    default Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }

    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

}
