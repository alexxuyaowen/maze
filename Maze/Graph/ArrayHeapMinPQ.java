package TheGame.Graph;

import java.util.NoSuchElementException;
import java.util.HashMap;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    private HashMap<T, Integer> index = new HashMap<>();
    private T[] pq;
    private double[] priorities;
    private int size;

    public ArrayHeapMinPQ() {
        this.pq = (T[]) new Object[1];
        this.priorities = new double[1];
        this.size = 0;
    }

    /* Adds an item with the given priority value. Throws an
     * IllegalArgumentException if item is already present.
     * You may assume that item is never null. */
    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException();
        }

        if (size >= pq.length - 1) {
            resize(pq.length * 2);
        }

        size++;
        pq[size] = item;
        priorities[size] = priority;
        index.put(item, size);
        swim(size);
    }

    private void resize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        double[] tempP = new double[capacity];
        /* for (int i = 1; i <= size; i++) {
            temp[i] = pq[i];
            tempP[i] = priorities[i];
        } */

        System.arraycopy(pq, 0, temp, 0, size + 1);
        System.arraycopy(priorities, 0, tempP, 0, size + 1);

        pq = temp;
        priorities = tempP;
    }

    /* Returns true if the PQ contains the given item. */
    @Override
    public boolean contains(T item) {
        return index.containsKey(item);
    }

    /* Returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    @Override
    public T getSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return pq[1];
    }

    /* Removes and returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    @Override
    public T removeSmallest() {
        T min = getSmallest();
        swap(1, size);
        pq[size] = null;
        // priorities[size] = 0.0;
        size--;
        sink(1);
        if (size > 0 && size <= (pq.length - 1) / 4) {
            resize(pq.length / 2);
        }
        return min;
    }

    private void swap(int i, int j) {
        T temp = pq[i];
        pq[i] = pq[j];
        pq[j] = temp;

        double tempP = priorities[i];
        priorities[i] = priorities[j];
        priorities[j] = tempP;

        int tempI = index.get(pq[i]);
        // index.remove(pq[i]);
        // index.remove(pq[j]);
        index.put(pq[i], index.get(pq[j]));
        index.put(pq[j], tempI);
    }

    /* Returns the number of items in the PQ. */
    @Override
    public int size() {
        return size;
    }
    /* Changes the priority of the given item. Throws NoSuchElementException if the item
     * doesn't exist. */
    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }

       /* if (priority < 0) {
            throw new IndexOutOfBoundsException();
        } */

        int i = index.get(item);
        priorities[i] = priority;

        sink(i);
        swim(i);
    }

    private void swim(int k) {
        int parent = parent(k);
        if (k > 1 && priorities[k] < priorities[parent]) {
            swap(k, parent);
            swim(parent);
        }
    }

    private void sink(int k) {
        if (k * 2 > size) {
            return;
        } // the base case to ensure that the left child always exists

        int theChild = rightChild(k); // assume theChild is on the right
        if (pq[theChild] == null || priorities[leftChild(k)] < priorities[theChild]) {
            theChild = leftChild(k);
        } // if the right child is null or has higher priority than that of the left,
        // then theChild is on the left

        if (priorities[k] > priorities[theChild]) {
            swap(k, theChild);
            sink(theChild);
        }
    }

    private int parent(int k) {
        return k / 2;
    }

    private int leftChild(int k) {
        return k * 2;
    }

    private int rightChild(int k) {
        return k * 2 + 1;
    }
}
