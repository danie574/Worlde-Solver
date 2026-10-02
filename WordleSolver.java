import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordleSolver {

    private static final int WORD_LENGTH = 5;
    private static final String DICTIONARY_FILE = "wordle-valid-words.txt";
    private static final String OUTPUT_FILE = "possible_words.txt";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Load the dictionary once when the program starts.
        List<String> dictionary = loadDictionary();

        if (dictionary.isEmpty()) {
            System.out.println("No words were loaded.");
            System.out.println(
                    "Make sure wordle-valid-words.txt is in the project directory."
            );
            scanner.close();
            return;
        }

        System.out.println("=================================");
        System.out.println("        WORDLE WORD SOLVER");
        System.out.println("=================================");
        System.out.println();

        System.out.println(
                "Loaded " + dictionary.size()
                        + " five-letter words."
        );

        System.out.println();

        // '_' means the position is unknown.
        char[] knownLetters = {
                '_', '_', '_', '_', '_'
        };

        // Stores letters that we know are NOT in the word.
        List<Character> excludedLetters = new ArrayList<>();

        boolean running = true;

        while (running) {

            displayPattern(knownLetters);
            displayExcludedLetters(excludedLetters);

            System.out.println();
            System.out.println("1 - Set first letter");
            System.out.println("2 - Set second letter");
            System.out.println("3 - Set third letter");
            System.out.println("4 - Set fourth letter");
            System.out.println("5 - Set fifth letter");
            System.out.println("6 - Exclude a letter");
            System.out.println("7 - Generate / Print candidates");
            System.out.println("q - Quit");

            System.out.print("Choice: ");

            String choice = scanner.nextLine()
                    .trim()
                    .toLowerCase();

            // =========================
            // QUIT
            // =========================

            if (choice.equals("q")) {
                running = false;
                continue;
            }

            // =========================
            // GENERATE / PRINT
            // =========================

            if (choice.equals("7")) {

                System.out.println();
                System.out.println(
                        "Searching dictionary..."
                );

                List<String> candidates =
                        findCandidates(
                                dictionary,
                                knownLetters,
                                excludedLetters
                        );

                System.out.println(
                        "Found "
                                + candidates.size()
                                + " possible words."
                );

                // Display candidates
                displayMatrix(candidates);

                // Save candidates
                writeToFile(candidates);

                System.out.println();

                continue;
            }

            // =========================
            // EXCLUDE LETTER
            // =========================

// =========================
// EXCLUDE LETTERS
// =========================

if (choice.equals("6")) {

    System.out.print(
            "Enter letters that are NOT in the word: "
    );

    String input = scanner.nextLine()
            .trim()
            .toLowerCase();

    if (input.isEmpty()) {

        System.out.println();
        System.out.println(
                "Please enter at least one letter."
        );
        System.out.println();

        continue;
    }

    boolean addedAny = false;

    for (char letter : input.toCharArray()) {

        // Ignore spaces
        if (Character.isWhitespace(letter)) {
            continue;
        }

        // Make sure it is a letter
        if (!Character.isLetter(letter)) {

            System.out.println(
                    "Ignoring invalid character: '"
                            + letter
                            + "'."
            );

            continue;
        }

        // Don't add the same letter twice
        if (!excludedLetters.contains(letter)) {

            excludedLetters.add(letter);
            addedAny = true;
        }
    }

    System.out.println();

    if (addedAny) {

        System.out.println(
                "Excluded letters updated."
        );

        displayExcludedLetters(excludedLetters);

    } else {

        System.out.println(
                "No new letters were added."
        );
    }

    System.out.println();

    continue;
}

            // =========================
            // SET LETTER
            // =========================

            if (!choice.matches("[1-5]")) {

                System.out.println();
                System.out.println(
                        "Invalid choice."
                );
                System.out.println();

                continue;
            }

            int position =
                    Integer.parseInt(choice) - 1;

            System.out.print(
                    "Enter confirmed letter for position "
                            + (position + 1)
                            + ": "
            );

            String input = scanner.nextLine()
                    .trim()
                    .toLowerCase();

            // Validate input
            if (input.length() != 1 ||
                    !Character.isLetter(input.charAt(0))) {

                System.out.println();
                System.out.println(
                        "Please enter exactly one letter."
                );
                System.out.println();

                continue;
            }

            // Save confirmed letter
            knownLetters[position] =
                    input.charAt(0);

            System.out.println();
            System.out.println(
                    "Position "
                            + (position + 1)
                            + " confirmed as '"
                            + input
                            + "'."
            );

            System.out.println();
        }

        scanner.close();

        System.out.println();
        System.out.println(
                "Wordle Solver closed."
        );
    }

    /**
     * Loads the dictionary and keeps only valid
     * five-letter English words.
     */
    private static List<String> loadDictionary() {

        List<String> words = new ArrayList<>();

        try {

            List<String> lines =
                    Files.readAllLines(
                            Path.of(DICTIONARY_FILE)
                    );

            for (String line : lines) {

                String word =
                        line.trim().toLowerCase();

                // Only accept exactly five
                // alphabetic characters.
                if (word.length() == WORD_LENGTH &&
                        word.matches("[a-z]+")) {

                    words.add(word);
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Could not read "
                            + DICTIONARY_FILE
            );

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }

        return words;
    }

    /**
     * Searches the dictionary for words matching
     * the confirmed letters and excluding
     * all known incorrect letters.
     */
    private static List<String> findCandidates(
            List<String> dictionary,
            char[] knownLetters,
            List<Character> excludedLetters) {

        List<String> candidates =
                new ArrayList<>();

        for (String word : dictionary) {

            boolean matches = true;

            // =========================
            // CHECK CONFIRMED LETTERS
            // =========================

            for (int i = 0; i < WORD_LENGTH; i++) {

                // If we know a letter at this position,
                // the dictionary word must have the same
                // letter there.
                if (knownLetters[i] != '_' &&
                        word.charAt(i) != knownLetters[i]) {

                    matches = false;
                    break;
                }
            }

            if (!matches) {
                continue;
            }

            // =========================
            // CHECK EXCLUDED LETTERS
            // =========================

            for (char excludedLetter : excludedLetters) {

                if (word.indexOf(excludedLetter) != -1) {

                    matches = false;
                    break;
                }
            }

            if (matches) {
                candidates.add(word);
            }
        }

        return candidates;
    }

    /**
     * Displays the current Wordle pattern.
     */
    private static void displayPattern(
            char[] knownLetters) {

        System.out.print(
                "Current pattern: "
        );

        for (char letter : knownLetters) {

            System.out.print(
                    letter + " "
            );
        }

        System.out.println();
    }

    /**
     * Displays the letters known to be absent.
     */
    private static void displayExcludedLetters(
            List<Character> excludedLetters) {

        System.out.print(
                "Excluded letters: "
        );

        if (excludedLetters.isEmpty()) {

            System.out.println("none");

            return;
        }

        for (char letter : excludedLetters) {

            System.out.print(
                    letter + " "
            );
        }

        System.out.println();
    }

    /**
     * Displays the candidate words in a matrix.
     */
    private static void displayMatrix(
            List<String> candidates) {

        System.out.println();
        System.out.println(
                "================================="
        );

        System.out.println(
                "       POSSIBLE WORDS"
        );

        System.out.println(
                "================================="
        );

        System.out.println();

        if (candidates.isEmpty()) {

            System.out.println(
                    "No matching words found."
            );

            return;
        }

        // Number of words per row.
        int columns = 10;

        // Width of each column.
        int columnWidth = 8;

        for (int i = 0;
             i < candidates.size();
             i++) {

            System.out.printf(
                    "%-" + columnWidth + "s",
                    candidates.get(i)
            );

            if ((i + 1) % columns == 0) {
                System.out.println();
            }
        }

        // Make sure the last row gets
        // a newline.
        if (candidates.size() % columns != 0) {
            System.out.println();
        }

        System.out.println();
    }

    /**
     * Writes the candidate words to
     * possible_words.txt.
     */
    private static void writeToFile(
            List<String> candidates) {

        try {

            Files.write(
                    Path.of(OUTPUT_FILE),
                    candidates
            );

            System.out.println(
                    "Results saved to "
                            + OUTPUT_FILE
            );

        } catch (IOException e) {

            System.out.println(
                    "Could not write "
                            + OUTPUT_FILE
            );

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }
}
