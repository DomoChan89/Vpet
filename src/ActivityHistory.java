/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: ActivityHistory.java
* @author Dominique Wigginton
* Description:
*   Remembers every action the player takes in a MyLinkedList<Activity>, and the 5 most
*   recent in a MyQueue<Activity>. The history can be shown in order, searched by keyword,
*   sorted by action, and counted. It belongs to the player, so it keeps every pet's
*   actions, even after a pet dies.
*
* Inputs:  Pets and the commands the player types (through record()).
* Outputs: The history printed to the console: full, recent, search results, sorted.
*
* Change log:
*   Date        Description
*   2026-11-19  Created. Overloaded record() methods, search (linear), sortedByAction
*               (insertion sort), countFor (recursive count), and the recent-actions queue.
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*/

import java.util.Comparator;

public class ActivityHistory {
    /** How many recent actions the queue keeps. */
    public static final int RECENT_LIMIT = 5;

    private final MyLinkedList<Activity> all = new MyLinkedList<>();
    private final MyQueue<Activity> recent = new MyQueue<>();

    /** Records an action for a pet, using the pet's name and current age as the turn. */
    public void record(Pet pet, String command) {
        record(pet.getName(), pet.getAge(), command);
    }

    /**
     * Records an action (overloaded version that takes the details directly).
     * The action goes at the end of the full history and the back of the recent queue.
     * When the queue has more than RECENT_LIMIT actions, the oldest one is dequeued.
     */
    public void record(String petName, int turn, String command) {
        Activity activity = new Activity(petName, turn, command);
        all.add(activity);
        recent.enqueue(activity);
        while (recent.size() > RECENT_LIMIT) {
            recent.dequeue();
        }
    }

    /** Linear search: every activity whose pet name or command contains the keyword. */
    public MyLinkedList<Activity> search(String keyword) {
        return all.filter(activity -> activity.matches(keyword));
    }

    /**
     * Insertion sort: a copy of the history grouped alphabetically by action
     * (all "bathe", then all "feed", ...). Because the sort is stable, each group
     * stays in turn order.
     */
    public MyLinkedList<Activity> sortedByAction() {
        return all.insertionSort(Comparator.comparing(Activity::getAction));
    }

    /** Recursive count: how many actions were taken with the named pet. */
    public int countFor(String petName) {
        return all.count(activity -> activity.getPetName().equalsIgnoreCase(petName));
    }

    /** Returns how many actions have been recorded in total. */
    public int size() {
        return all.size();
    }

    /** Returns true if nothing has been recorded yet. */
    public boolean isEmpty() {
        return all.isEmpty();
    }

    /** Returns the full history in the order things happened. */
    public MyLinkedList<Activity> getAll() {
        return all;
    }

    /** Returns the recent-actions queue (oldest first). */
    public MyQueue<Activity> getRecent() {
        return recent;
    }

    /** Prints a titled list of activities, or a message if there are none. */
    public static void display(String title, Iterable<Activity> activities) {
        System.out.println("\n=== " + title + " ===");
        boolean any = false;
        for (Activity activity : activities) {
            System.out.println("  " + activity);
            any = true;
        }
        if (!any) {
            System.out.println("  No activities have been recorded yet.");
        }
    }
}
