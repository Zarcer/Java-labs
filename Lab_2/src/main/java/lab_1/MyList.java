package lab_1;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyList<T> implements Iterable<T> {
    private Node<T> head;
    public int size;
    public MyList() {
        head = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<T> {
        private Node<T> cursor = head;

        @Override
        public boolean hasNext() {
            return cursor != null;
        }

        @Override
        public T next() {
            if (cursor == null) {
                throw new NoSuchElementException();
            }
            T v = cursor.data;
            cursor = cursor.next;
            return v;
        }
    }

    public void add(T value) {
        Node<T> n = new Node<>(value);
        Object syncOn;
        if(head!=null){
            syncOn=head.lock;
        }
        else{
            syncOn=this;
        }
        synchronized (syncOn) {
            n.next = head;
            head = n;
            size++;
        }
    }

    public synchronized T get(int index) {
        checkIndex(index);
        Node<T> cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.data;
    }

    private void checkIndex(int idx) {
        if (idx < 0 || idx >= size) {
            throw new IndexOutOfBoundsException("Index " + idx + " Size " + size);
        }
    }

    public void swap(int index) {
        if (index < 0 || index >= size - 1) {
            throw new IndexOutOfBoundsException("Index " + index + " Size " + size);
        }
        Node<T> prev = null;
        Node<T> first;
        Node<T> second;
        synchronized (this) {
            if (index == 0) {
                first = head;
                second = head.next;
            } else {
                prev = head;
                for (int i = 0; i < index - 1; i++) {
                    prev=prev.next;
                }
                first = prev.next;
                second = first.next;
            }
        }

        Object lockPrev;
        if(prev!=null){
            lockPrev=prev.lock;
        }
        else{
            lockPrev=null;
        }
        Object lockFirst = first.lock;
        Object lockSecond = second.lock;

        if (lockPrev != null) {
            synchronized (lockPrev) {
                synchronized (lockFirst) {
                    synchronized (lockSecond) {
                        performSwap(prev, first, second, false);
                    }
                }
            }
        } else {
            synchronized (lockFirst) {
                synchronized (lockSecond) {
                    performSwap(null, first, second, true);
                }
            }
        }
    }

    private void performSwap(Node<T> prev, Node<T> a, Node<T> b, boolean aWasHead) {
        a.next = b.next;
        b.next = a;
        if (aWasHead) {
            head = b;
        } else {
            prev.next = b;
        }
    }

    private static class Node<E> {
        final E data;
        Node<E> next;
        final Object lock = new Object();
        Node(E data) {
            this.data = data;
            this.next = null;
        }
    }
}
