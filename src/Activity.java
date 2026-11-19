/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: Activity.java
* @author Dominique Wigginton
* Description:
*   One entry in the activity history: which pet, on which turn, did which action (the
*   command word), and the full command the player typed.
*
* Inputs:  Pet name, turn number, and the command the player typed.
* Outputs: A one-line description through toString(), and keyword matches.
*
* Change log:
*   Date        Description
*   2026-11-19  Created to store one record in ActivityHistory.
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*/
public class Activity {
    private final String petName;
    private final int turn;
    private final String action;   // the command word, e.g. "feed"
    private final String command;  // everything the player typed, e.g. "feed bacon"

    /** Creates a record. The action is the first word of the command, in lower case. */
    public Activity(String petName, int turn, String command) {
        this.petName = petName;
        this.turn = turn;
        this.command = command.trim();
        this.action = this.command.split("\\s+")[0].toLowerCase();
    }

    public String getPetName() { return petName; }
    public int getTurn() { return turn; }
    public String getAction() { return action; }
    public String getCommand() { return command; }

    /** Returns true if the keyword appears in the pet's name or the command (ignores upper/lower case). */
    public boolean matches(String keyword) {
        String word = keyword.toLowerCase();
        return petName.toLowerCase().contains(word) || command.toLowerCase().contains(word);
    }

    /** One line for the history list, e.g. "Turn   3 | Rex     | feed bacon". */
    @Override
    public String toString() {
        return String.format("Turn %3d | %-10s | %s", turn, petName, command);
    }
}
