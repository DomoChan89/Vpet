/**
 * A pet's hunger meter, like the hunger hearts on a Tamagotchi. 100 means full and
 * 0 means empty. The meter drains over time and with activity (training, games, play),
 * and food fills it back up. If the meter stays empty for STARVE_TURNS turns in a row,
 * the pet starves.
 *
 * Change log:
 *   2026-10-22  Created as an empty placeholder.
 *   2026-10-26  Added class comment.
 *   2026-10-26  Now the hunger meter: 100 = full, 0 = empty, with a starvation countdown.
 */
public class Hunger {
    public static final int FULL = 100;
    /** How many turns a pet can go with an empty meter before it starves. */
    public static final int STARVE_TURNS = 10;
    private static final int DRAIN_PER_TURN = 5;
    private static final int HUNGRY_BELOW = 30;

    private int level = 80;
    private int turnsEmpty = 0;

    /** Returns how full the pet is, from 0 (empty) to 100 (full). */
    public int getLevel() {
        return level;
    }

    /** Returns true when the meter is full and the pet won't eat. */
    public boolean isFull() {
        return level >= FULL;
    }

    /** Returns true when the meter is low (under 30) and the pet wants food. */
    public boolean isHungry() {
        return level < HUNGRY_BELOW;
    }

    /** Returns true when the meter is completely empty. */
    public boolean isEmpty() {
        return level <= 0;
    }

    /** Fills the meter by the given amount (up to full). Any food stops the starvation countdown. */
    public void fill(int amount) {
        level = Math.min(FULL, level + amount);
        if (level > 0) turnsEmpty = 0;
    }

    /** Fills the meter all the way up. */
    public void fillCompletely() {
        fill(FULL);
    }

    /** Drains the meter by the given amount (down to empty), e.g. from training or playing. */
    public void drain(int amount) {
        level = Math.max(0, level - amount);
    }

    /** One turn passes: the meter drains a little, and if it's empty the starvation countdown goes on. */
    public void tick() {
        drain(DRAIN_PER_TURN);
        if (isEmpty()) turnsEmpty++;
    }

    /** Returns true once the meter has been empty for STARVE_TURNS turns in a row. */
    public boolean hasStarved() {
        return turnsEmpty >= STARVE_TURNS;
    }

    /** Returns how many more empty turns the pet can survive. */
    public int turnsUntilStarved() {
        return Math.max(0, STARVE_TURNS - turnsEmpty);
    }

    /** Draws the meter as a bar, e.g. "[#######---] 70/100". */
    @Override
    public String toString() {
        int filled = (level + 9) / 10;
        return "[" + "#".repeat(filled) + "-".repeat(10 - filled) + "] " + level + "/" + FULL;
    }
}
