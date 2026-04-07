package cz.upce.boop.ex.collection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BGenericList<T> implements BList<T> {

    protected T[] data;

    public BGenericList() {
        data = (T[]) new Object[0];
    }

    @Override
    public String toString() {
        return "[" + stream().map(Object::toString).reduce("", (r, e) -> r + "," + e).substring(1) + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BGenericList<?>)) {
            return false;
        }
        final BGenericList<?> other = (BGenericList<?>) obj;
        return Arrays.deepEquals(this.data, other.data);
    }

    @Override
    public void add(T o) {
        T[] oldData = data;
        data = (T[]) new Object[data.length + 1];
        for (int i = 0; i < oldData.length; i++) {
            data[i] = oldData[i];
        }

        data[data.length - 1] = o;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= data.length) {
            throw new IllegalArgumentException("index");
        }

        return data[index];
    }

    @Override
    public T set(int index, T element) {
        if (index < 0 || index >= data.length) {
            throw new IllegalArgumentException("index");
        }

        T old = data[index];
        data[index] = element;
        return old;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= data.length) {
            throw new IllegalArgumentException("index");
        }

        T old = data[index];

        T[] oldData = data;
        data = (T[]) new Object[data.length - 1];
        for (int i = 0; i < oldData.length; i++) {
            if (i == index) {
                continue;
            }

            data[i >= index ? i - 1 : i] = oldData[i];
        }

        return old;
    }

    @Override
    public BIterator<T> iterator() {
        return new BIterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size() && size() > 0;
            }

            @Override
            public T next() {
                T result = get(index);
                index++;
                return result;
            }

            @Override
            public void remove() {
                BGenericList.this.remove(index - 1);
                index--;
            }
        };
    }

    public void sort(Comparator<? super T> comparator) {
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size() - 1; j++) {
                T left = get(j);
                T right = get(j + 1);

                if (comparator.compare(left, right) > 0) {
                    set(j, right);
                    set(j + 1, left);
                }
            }
        }
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Spliterator<T> spliterator() {
        //return Spliterators.spliterator(iterator(), size(), Spliterator.SIZED);
        return new GenericListSpliterator(0, size());
    }

    private class GenericListSpliterator implements Spliterator<T> {

        private int index = 0;
        private int remainingSize = 0;

        public GenericListSpliterator(int index, int remainingSize) {
            this.index = index;
            this.remainingSize = remainingSize;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (remainingSize <= 0) {
                return false;
            }

            action.accept(data[index]);
            index++;
            remainingSize--;
            return true;
        }

        @Override
        public Spliterator<T> trySplit() {
            int splitSize = remainingSize / 2;

            index += splitSize;
            remainingSize -= splitSize;

            return new GenericListSpliterator(index, splitSize);
        }

        @Override
        public long estimateSize() {
            return remainingSize;
        }

        @Override
        public int characteristics() {
            return SIZED;
        }

    }

    public BList<T> asReadOnly() {
        return new BReadOnlyList<>(data);
    }

    public static class BReadOnlyList<T> extends BGenericList<T> {

        private BReadOnlyList(T[] data) {
            // immutability !
            T[] readOnlyData = (T[]) new Object[data.length];
            for (int i = 0; i < readOnlyData.length; i++) {
                readOnlyData[i] = data[i];
            }

            this.data = readOnlyData;
        }

        @Override
        public T set(int index, T element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(T o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BIterator<T> iterator() {
            return new BIterator<T>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < size() && size() > 0;
                }

                @Override
                public T next() {
                    T result = get(index);
                    index++;
                    return result;
                }

            };
        }

    }

}