/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: MyLinkedList.java
* @author Dominique Wigginton
* Description:
*   A generic singly linked list written from scratch (no java.util collections). The game
*   uses it for the activity history, the memorial of pets that died, the food in the
*   player's bag, trick progress, and the shop's stock. MyQueue is built on top of it.
*
*   Algorithms:
*   - Linear search (iterative):  indexOf(), findFirst(), filter()
*   - Recursion:                  contains(), count()
*   - Merge sort (recursive):     mergeSort()
*   - Insertion sort (iterative): insertionSort()
*
* Inputs:  Items of any type T added by the calling code.
* Outputs: Items returned by get(), removeFirst(), search results, sorted copies.
*
* Change log:
*   Date        Description
*   2026-11-19  Created. Node class, add/addFirst/removeFirst/remove/get/size,
*               linear search, recursive contains() and count(), filter(),
*               recursive merge sort, iterative insertion sort, and an iterator
*               so the list works in for-each loops.
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*
* @param <T> the type of item stored in the list
*/

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class MyLinkedList<T> implements Iterable<T> {

    /** One link in the chain: an item and a pointer to the next node. */
    private static class Node<T> {
        private T data;
        private Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;   // first node, or null if the list is empty
    private Node<T> tail;   // last node, so add() doesn't have to walk the list
    private int size;

    /** Creates an empty list. */
    public MyLinkedList() {
        // head and tail start as null, size starts at 0
    }

    // ------------------------------------------------------------------
    // Adding and removing
    // ------------------------------------------------------------------

    /** Adds an item to the end of the list. */
    public void add(T item) {
        Node<T> node = new Node<>(item);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
    }

    /** Adds an item to the front of the list. */
    public void addFirst(T item) {
        Node<T> node = new Node<>(item);
        node.next = head;
        head = node;
        if (tail == null) {
            tail = node;
        }
        size++;
    }

    /**
     * Removes and returns the first item.
     *
     * @throws NoSuchElementException if the list is empty
     */
    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("Cannot remove from an empty list.");
        }
        T item = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return item;
    }

    /**
     * Removes the first node holding this exact item (compared with equals()).
     * Returns true if something was removed, false if the item wasn't in the list.
     */
    public boolean remove(T item) {
        Node<T> previous = null;
        Node<T> current = head;
        while (current != null) {
            if (equalItems(current.data, item)) {
                if (previous == null) {
                    head = current.next;
                } else {
                    previous.next = current.next;
                }
                if (current == tail) {
                    tail = previous;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    /** Removes every item from the list. */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    // ------------------------------------------------------------------
    // Reading
    // ------------------------------------------------------------------

    /**
     * Returns the item at a position (0 = first).
     *
     * @throws IndexOutOfBoundsException if index is below 0 or not less than size()
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of range for size " + size + ".");
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    /**
     * Returns the first item without removing it.
     *
     * @throws NoSuchElementException if the list is empty
     */
    public T getFirst() {
        if (head == null) {
            throw new NoSuchElementException("The list is empty.");
        }
        return head.data;
    }

    /** Returns how many items are in the list. */
    public int size() {
        return size;
    }

    /** Returns true if the list has no items. */
    public boolean isEmpty() {
        return size == 0;
    }

    // ------------------------------------------------------------------
    // Searching
    // ------------------------------------------------------------------

    /** Linear search: returns the position of the first matching item, or -1 if it isn't there. */
    public int indexOf(T item) {
        int index = 0;
        for (Node<T> current = head; current != null; current = current.next) {
            if (equalItems(current.data, item)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /** Linear search: returns the first item that passes the test, or null if none do. */
    public T findFirst(Predicate<T> test) {
        for (Node<T> current = head; current != null; current = current.next) {
            if (test.test(current.data)) {
                return current.data;
            }
        }
        return null;
    }

    /** Linear search: returns a new list of every item that passes the test, in the same order. */
    public MyLinkedList<T> filter(Predicate<T> test) {
        MyLinkedList<T> matches = new MyLinkedList<>();
        for (Node<T> current = head; current != null; current = current.next) {
            if (test.test(current.data)) {
                matches.add(current.data);
            }
        }
        return matches;
    }

    /** Returns true if the item is in the list. Uses recursion (see containsFrom). */
    public boolean contains(T item) {
        return containsFrom(head, item);
    }

    /**
     * Recursive helper for contains().
     * Base cases: an empty rest of the list (not found), or the current node matches (found).
     * Recursive case: check the rest of the list after this node.
     */
    private boolean containsFrom(Node<T> node, T item) {
        if (node == null) {
            return false;
        }
        if (equalItems(node.data, item)) {
            return true;
        }
        return containsFrom(node.next, item);
    }

    /** Counts how many items pass the test. Uses recursion (see countFrom). */
    public int count(Predicate<T> test) {
        return countFrom(head, test);
    }

    /**
     * Recursive helper for count().
     * Base case: an empty rest of the list has 0 matches.
     * Recursive case: (1 if this node matches, else 0) + matches in the rest of the list.
     */
    private int countFrom(Node<T> node, Predicate<T> test) {
        if (node == null) {
            return 0;
        }
        int here = test.test(node.data) ? 1 : 0;
        return here + countFrom(node.next, test);
    }

    // ------------------------------------------------------------------
    // Sorting (both return a sorted copy; the original list is not changed)
    // ------------------------------------------------------------------

    /**
     * Recursive merge sort. Returns a new list sorted by the comparator.
     * Splits the list in half, sorts each half recursively, then merges them.
     * Runs in O(n log n) time. Stable: equal items keep their original order.
     */
    public MyLinkedList<T> mergeSort(Comparator<T> order) {
        MyLinkedList<T> sorted = new MyLinkedList<>();
        Node<T> sortedHead = mergeSortNodes(copyNodes(head), order);
        for (Node<T> current = sortedHead; current != null; current = current.next) {
            sorted.add(current.data);
        }
        return sorted;
    }

    /**
     * Recursive step of merge sort.
     * Base case: a chain of 0 or 1 nodes is already sorted.
     * Recursive case: split in half, sort both halves, merge.
     */
    private Node<T> mergeSortNodes(Node<T> first, Comparator<T> order) {
        if (first == null || first.next == null) {
            return first;
        }
        Node<T> middle = findMiddle(first);
        Node<T> secondHalf = middle.next;
        middle.next = null; // cut the chain in two

        Node<T> left = mergeSortNodes(first, order);
        Node<T> right = mergeSortNodes(secondHalf, order);
        return merge(left, right, order);
    }

    /** Finds the last node of the first half using a slow pointer and a fast pointer. */
    private Node<T> findMiddle(Node<T> first) {
        Node<T> slow = first;
        Node<T> fast = first.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    /** Merges two sorted chains into one sorted chain. Takes from the left on ties to stay stable. */
    private Node<T> merge(Node<T> left, Node<T> right, Comparator<T> order) {
        Node<T> dummy = new Node<>(null);
        Node<T> end = dummy;
        while (left != null && right != null) {
            if (order.compare(left.data, right.data) <= 0) {
                end.next = left;
                left = left.next;
            } else {
                end.next = right;
                right = right.next;
            }
            end = end.next;
        }
        end.next = (left != null) ? left : right;
        return dummy.next;
    }

    /** Makes a copy of a chain of nodes so sorting never changes the original list. */
    private Node<T> copyNodes(Node<T> first) {
        Node<T> dummy = new Node<>(null);
        Node<T> end = dummy;
        for (Node<T> current = first; current != null; current = current.next) {
            end.next = new Node<>(current.data);
            end = end.next;
        }
        return dummy.next;
    }

    /**
     * Iterative insertion sort. Returns a new list sorted by the comparator.
     * Takes each item in turn and walks the sorted list to find where it belongs.
     * Runs in O(n^2) time, which is fine for the small lists in this game.
     * Stable: an item is placed after any equal items already in the sorted list.
     */
    public MyLinkedList<T> insertionSort(Comparator<T> order) {
        MyLinkedList<T> sorted = new MyLinkedList<>();
        for (Node<T> current = head; current != null; current = current.next) {
            sorted.insertInOrder(current.data, order);
        }
        return sorted;
    }

    /** Puts one item into an already-sorted list at the right place (used by insertionSort). */
    private void insertInOrder(T item, Comparator<T> order) {
        if (head == null || order.compare(item, head.data) < 0) {
            addFirst(item);
            return;
        }
        Node<T> current = head;
        while (current.next != null && order.compare(current.next.data, item) <= 0) {
            current = current.next;
        }
        Node<T> node = new Node<>(item);
        node.next = current.next;
        current.next = node;
        if (node.next == null) {
            tail = node;
        }
        size++;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Compares two items with equals(), treating two nulls as equal. */
    private boolean equalItems(T a, T b) {
        return (a == null) ? b == null : a.equals(b);
    }

    /** Lets the list be used in a for-each loop: for (T item : list) { ... } */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T item = current.data;
                current = current.next;
                return item;
            }
        };
    }

    /** Lists the items in order, e.g. "[Rex, Tom, Tweety]". */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("[");
        for (Node<T> current = head; current != null; current = current.next) {
            out.append(current.data);
            if (current.next != null) {
                out.append(", ");
            }
        }
        return out.append("]").toString();
    }
}
