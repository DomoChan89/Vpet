import java.util.List;

/**
 * Draws pixel-art sprites in the console. A sprite is a list of text rows where
 * each letter is one colored pixel (see color()) and '.' is an empty pixel.
 * Each pixel is printed as two colored spaces so it looks roughly square.
 *
 * Change log:
 *   2026-10-26  Created to show pixel-art animals in the adoption center.
 *   2026-10-26  Added mirror() and an indent option to render() for the Left or Right game.
 */
public final class PixelArt {
    private static final String RESET = "\u001B[0m";

    // Set to false (run the game with --plain) if your console shows odd symbols instead of colors.
    private static boolean useColor = true;

    private PixelArt() {
    }

    /** Turns colors on or off. With colors off, pixels are drawn as "##". */
    public static void setUseColor(boolean enabled) {
        useColor = enabled;
    }

    /** Returns the 256-color console code for a pixel letter, or -1 for an empty pixel. */
    private static int color(char pixel) {
        return switch (pixel) {
            case 'K' -> 16;   // black
            case 'W' -> 231;  // white
            case 'B' -> 136;  // brown
            case 'D' -> 94;   // dark brown
            case 'P' -> 211;  // pink
            case 'O' -> 208;  // orange
            case 'Y' -> 220;  // yellow
            case 'G' -> 70;   // green
            case 'E' -> 22;   // dark green
            case 'L' -> 39;   // blue
            case 'R' -> 160;  // red
            case 'A' -> 245;  // gray
            default -> -1;    // '.' or anything unknown = empty
        };
    }

    /** Draws one pixel as two characters wide. */
    private static String pixel(char p) {
        int code = color(p);
        if (code < 0) return "  ";
        if (!useColor) return "##";
        return "\u001B[48;5;" + code + "m  " + RESET;
    }

    /** Draws one row of a sprite, or blank space if the sprite is shorter than the others. */
    private static String row(String[] sprite, int rowIndex, int width) {
        if (rowIndex >= sprite.length) return " ".repeat(width * 2);
        StringBuilder line = new StringBuilder();
        for (char p : sprite[rowIndex].toCharArray()) {
            line.append(pixel(p));
        }
        return line.toString();
    }

    /** Draws a single sprite. */
    public static String render(String[] sprite) {
        return render(sprite, 0);
    }

    /** Draws a single sprite, shifted right by the given number of spaces. */
    public static String render(String[] sprite, int indent) {
        String pad = " ".repeat(indent);
        StringBuilder out = new StringBuilder();
        for (int r = 0; r < sprite.length; r++) {
            out.append(pad).append(row(sprite, r, sprite[0].length())).append('\n');
        }
        return out.toString();
    }

    /** Returns a copy of the sprite flipped left-to-right, so it faces the other way. */
    public static String[] mirror(String[] sprite) {
        String[] flipped = new String[sprite.length];
        for (int r = 0; r < sprite.length; r++) {
            flipped[r] = new StringBuilder(sprite[r]).reverse().toString();
        }
        return flipped;
    }

    /**
     * Draws several sprites next to each other, with an optional caption under each one.
     * All sprites should be the same width.
     */
    public static String renderSideBySide(List<String[]> sprites, List<String> captions) {
        int width = sprites.getFirst()[0].length();
        int height = sprites.stream().mapToInt(s -> s.length).max().orElse(0);
        String gap = "    ";
        StringBuilder out = new StringBuilder();

        for (int r = 0; r < height; r++) {
            for (int i = 0; i < sprites.size(); i++) {
                if (i > 0) out.append(gap);
                out.append(row(sprites.get(i), r, width));
            }
            out.append('\n');
        }

        if (!captions.isEmpty()) {
            for (int i = 0; i < captions.size(); i++) {
                if (i > 0) out.append(gap);
                out.append(String.format("%-" + (width * 2) + "s", captions.get(i)));
            }
            out.append('\n');
        }
        return out.toString();
    }
}
