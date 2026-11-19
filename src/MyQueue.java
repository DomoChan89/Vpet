/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: MyQueue.java
* @author Dominique Wigginton
* Description:
*   A generic first-in, first-out (FIFO) queue built on MyLinkedList. Items join at the
*   back (enqueue) and leave from the front (dequeue). The game uses it to keep the
*   player's 5 most recent actions: each new action is enqueued, and the oldest is
*   dequeued once there are more than 5.
*
* Inputs:  Items of any type T enqueued by the calling code.
* Outputs: Items returned by dequeue() and peek(), oldest first.
*
* Change log:
*   Date        Description
*   2026-11-19  Created. enqueue, dequeue, peek, size, isEmpty, and an iterator
*               that goes from the front of the queue to the back.
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*
* @param <T> the type of item stored in the queue
*/

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyQueue<T> implements Iterable<T> {
    private final MyLinkedList<T> items = new MyLinkedList<>(); // front of queue = first node

    /** Adds an item to the back of the queue. */
    public void enqueue(T item) {
        items.add(item);
    }

    /**
     * Removes and returns the item at the front of the queue (the oldest one).
     *
     * @throws NoSuchElementException if the queue is empty
     */
    public T dequeue() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Cannot dequeue from an empty queue.");
        }
        return items.removeFirst();
    }

    /**
     * Returns the item at the front of the queue without removing it.
     *
     * @throws NoSuchElementException if the queue is empty
     */
    public T peek() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Cannot peek at an empty queue.");
        }
        return items.getFirst();
    }

    /** Returns how many items are waiting in the queue. */
    public int size() {
        return items.size();
    }

    /** Returns true if the queue has no items. */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Goes through the queue from front (oldest) to back (newest) without removing anything. */
    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    /** Lists the items from front to back, e.g. "[feed bacon, play, sleep]". */
    @Override
    public String toString() {
        return items.toString();
    }
}
