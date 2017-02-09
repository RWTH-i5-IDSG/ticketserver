package de.rwth.idsg.barti.server;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MutableLoadBalancer<T> {
    private final Deque<T> toBalance = new LinkedList<>();

    public boolean hasNext() {
        return !toBalance.isEmpty();
    }

    public T next() throws NoSuchElementException {
        final T t = toBalance.removeFirst();
        toBalance.addLast(t);
        return t;
    }

    public void add(final T t) {
        toBalance.addLast(t);
    }

    public boolean remove(final T t) {
        return toBalance.remove(t);
    }
}
