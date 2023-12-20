package net.advancedplugins.utils.collections;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;

public class UniquePriorityQueue<E> extends PriorityQueue<E> {
    private final Set<E> set = new HashSet<>();

    public UniquePriorityQueue(Comparator<E> comparator) {
        super(comparator);
    }

    @Override
    public boolean offer(E e) {
        if (set.add(e)) {
            return super.offer(e);
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        return offer(e);
    }

    @Override
    public E poll() {
        E element = super.poll();
        if (element != null) {
            set.remove(element);
        }
        return element;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = super.remove(o);
        if (removed) {
            set.remove(o);
        }
        return removed;
    }
}