/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: DataStructureTest.java
* @author Dominique Wigginton
* Description:
*   Automatic tests for MyLinkedList, MyQueue, Inventory, and ActivityHistory. Each check
*   prints PASS or FAIL with what it tested, and the totals are printed at the end. Run
*   this class's main() on its own (it is separate from the game).
*
* Inputs:  None (all test data is built in the code).
* Outputs: PASS/FAIL lines and a summary, printed to the console. The exit code is 0 if every test passed and 1 if any failed.
*
* Change log:
*   Date        Description
*   2026-11-19  Created with tests for add/remove, search, recursion, both sorts,
*               the queue, the inventory, and the activity history.
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*/

import java.util.Comparator;
import java.util.NoSuchElementException;

public class DataStructureTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Data structure tests ===\n");
        testLinkedListBasics();
        testLinkedListErrors();
        testSearchAndRecursion();
        testSorting();
        testQueue();
        testInventory();
        testActivityHistory();

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        System.exit(failed == 0 ? 0 : 1);
    }

    /** Records and prints the result of one check. */
    private static void check(String description, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + description);
        } else {
            failed++;
            System.out.println("FAIL  " + description);
        }
    }

    /** Builds a list from the given items, in order. */
    @SafeVarargs
    private static <T> MyLinkedList<T> listOf(T... items) {
        MyLinkedList<T> list = new MyLinkedList<>();
        for (T item : items) {
            list.add(item);
        }
        return list;
    }

    private static void testLinkedListBasics() {
        System.out.println("-- MyLinkedList: add, get, remove (Objectives 5, 9, 14)");
        MyLinkedList<String> list = new MyLinkedList<>();
        check("new list is empty", list.isEmpty() && list.size() == 0);

        list.add("Rex");
        list.add("Tom");
        list.addFirst("Tweety");
        check("add + addFirst give [Tweety, Rex, Tom]", list.toString().equals("[Tweety, Rex, Tom]"));
        check("size is 3", list.size() == 3);
        check("get(1) is Rex", list.get(1).equals("Rex"));

        check("removeFirst returns Tweety", list.removeFirst().equals("Tweety"));
        check("remove(\"Tom\") returns true", list.remove("Tom"));
        check("remove(\"Nobody\") returns false", !list.remove("Nobody"));
        check("list is now [Rex]", list.toString().equals("[Rex]"));
        list.add("Spike");
        check("add after removing the tail still works: [Rex, Spike]", list.toString().equals("[Rex, Spike]"));

        int count = 0;
        for (String ignored : list) {
            count++;
        }
        check("for-each loop visits every item", count == 2);
    }

    private static void testLinkedListErrors() {
        System.out.println("\n-- MyLinkedList: error handling");
        MyLinkedList<String> empty = new MyLinkedList<>();
        try {
            empty.removeFirst();
            check("removeFirst on empty list throws", false);
        } catch (NoSuchElementException e) {
            check("removeFirst on empty list throws NoSuchElementException", true);
        }
        try {
            listOf("a", "b").get(5);
            check("get(5) on a 2-item list throws", false);
        } catch (IndexOutOfBoundsException e) {
            check("get(5) on a 2-item list throws IndexOutOfBoundsException", true);
        }
        try {
            listOf("a").get(-1);
            check("get(-1) throws", false);
        } catch (IndexOutOfBoundsException e) {
            check("get(-1) throws IndexOutOfBoundsException", true);
        }
    }

    private static void testSearchAndRecursion() {
        System.out.println("\n-- Searching (Objective 10) and recursion (Objective 7)");
        MyLinkedList<String> list = listOf("feed", "play", "feed", "sleep", "feed");
        check("indexOf(\"sleep\") is 3 (linear search)", list.indexOf("sleep") == 3);
        check("indexOf(\"jump\") is -1", list.indexOf("jump") == -1);
        check("contains(\"play\") is true (recursive)", list.contains("play"));
        check("contains(\"jump\") is false (recursive)", !list.contains("jump"));
        check("count of \"feed\" is 3 (recursive)", list.count(s -> s.equals("feed")) == 3);
        check("count on an empty list is 0", new MyLinkedList<String>().count(s -> true) == 0);
        check("filter keeps only matches", list.filter(s -> s.startsWith("f")).size() == 3);
        check("findFirst returns null when nothing matches", list.findFirst(s -> s.equals("jump")) == null);
    }

    private static void testSorting() {
        System.out.println("\n-- Sorting (Objective 10): merge sort (recursive) and insertion sort (iterative)");
        MyLinkedList<Integer> numbers = listOf(42, 7, 19, 3, 25, 7, 1);
        String expected = "[1, 3, 7, 7, 19, 25, 42]";

        check("mergeSort gives " + expected, numbers.mergeSort(Comparator.naturalOrder()).toString().equals(expected));
        check("insertionSort gives " + expected, numbers.insertionSort(Comparator.naturalOrder()).toString().equals(expected));
        check("sorting does not change the original list", numbers.toString().equals("[42, 7, 19, 3, 25, 7, 1]"));
        check("mergeSort descending", numbers.mergeSort(Comparator.<Integer>naturalOrder().reversed())
                .toString().equals("[42, 25, 19, 7, 7, 3, 1]"));
        check("mergeSort of an empty list is empty", new MyLinkedList<Integer>().mergeSort(Comparator.naturalOrder()).isEmpty());
        check("insertionSort of one item", listOf(5).insertionSort(Comparator.naturalOrder()).toString().equals("[5]"));

        // Stability: equal keys keep their original order.
        MyLinkedList<String> words = listOf("b1", "a1", "b2", "a2");
        Comparator<String> byLetter = Comparator.comparing(w -> w.charAt(0));
        check("insertionSort is stable", words.insertionSort(byLetter).toString().equals("[a1, a2, b1, b2]"));
        check("mergeSort is stable", words.mergeSort(byLetter).toString().equals("[a1, a2, b1, b2]"));
    }

    private static void testQueue() {
        System.out.println("\n-- MyQueue: first in, first out (Objectives 5, 9)");
        MyQueue<String> queue = new MyQueue<>();
        check("new queue is empty", queue.isEmpty());
        queue.enqueue("first");
        queue.enqueue("second");
        queue.enqueue("third");
        check("peek returns the oldest item", queue.peek().equals("first"));
        check("dequeue returns items in the order they went in",
                queue.dequeue().equals("first") && queue.dequeue().equals("second"));
        check("size is 1 after two dequeues", queue.size() == 1);
        queue.dequeue();
        try {
            queue.dequeue();
            check("dequeue on empty queue throws", false);
        } catch (NoSuchElementException e) {
            check("dequeue on empty queue throws NoSuchElementException", true);
        }
        try {
            queue.peek();
            check("peek on empty queue throws", false);
        } catch (NoSuchElementException e) {
            check("peek on empty queue throws NoSuchElementException", true);
        }
    }

    private static void testInventory() {
        System.out.println("\n-- Inventory on MyLinkedList (Objectives 4, 14)");
        Inventory bag = new Inventory(20);
        bag.addFood("Tuna");
        bag.addFood("Bacon");
        bag.addFood("bacon");
        check("countFood ignores upper/lower case (2 Bacon)", bag.countFood("BACON") == 2);
        check("bag lists food A-Z (insertion sort)", bag.toString().equals("Coins: 20 | Food: 2x Bacon, 1x Tuna"));
        check("useFood takes one out", bag.useFood("tuna") && bag.countFood("Tuna") == 0);
        check("useFood on missing food returns false", !bag.useFood("Steak"));
        check("spendCoins refuses when short", !bag.spendCoins(50) && bag.getCoins() == 20);
    }

    private static void testActivityHistory() {
        System.out.println("\n-- ActivityHistory (Objectives 6, 7, 9, 10)");
        ActivityHistory history = new ActivityHistory();
        history.record("Rex", 1, "feed bacon");
        history.record("Rex", 2, "play");
        history.record("Rex", 3, "bathe");
        history.record("Tom", 1, "feed tuna");
        history.record("Tom", 2, "play");
        history.record("Tom", 3, "sleep");
        history.record("Tom", 4, "feed chow");

        check("all 7 actions are kept", history.size() == 7);
        check("recent queue keeps only the last " + ActivityHistory.RECENT_LIMIT,
                history.getRecent().size() == ActivityHistory.RECENT_LIMIT);
        check("oldest recent action is Rex's bathe (turn 3)", history.getRecent().peek().getCommand().equals("bathe"));
        check("search \"feed\" finds 3", history.search("feed").size() == 3);
        check("search \"BACON\" ignores case and finds 1", history.search("BACON").size() == 1);
        check("search \"jump\" finds 0", history.search("jump").isEmpty());
        check("countFor(\"Tom\") is 4 (recursive)", history.countFor("Tom") == 4);

        MyLinkedList<Activity> sorted = history.sortedByAction();
        check("sortedByAction starts with bathe", sorted.get(0).getAction().equals("bathe"));
        check("feed group stays in turn order",
                sorted.get(1).getCommand().equals("feed bacon") && sorted.get(3).getCommand().equals("feed chow"));
    }
}
