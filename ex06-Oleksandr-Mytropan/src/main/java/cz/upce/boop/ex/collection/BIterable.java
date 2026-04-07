package cz.upce.boop.ex.collection;

@FunctionalInterface
public interface BIterable<T> extends Iterable<T> {

    BIterator<T> iterator();

}
