import java.util.Arrays;
import java.util.function.Predicate;

public class AscendingWordsFinder {

    /**
     * Finds words whose characters are in strictly ascending order
     * of character code.
     *
     * @param input a string containing words separated by whitespace
     * @return an array of words that satisfy the ascending-order condition
     */
    public static String[] findAscendingWords(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }

        Predicate<String> isAscending = word -> {
            for (int i = 0; i < word.length() - 1; i++) {
                if (word.charAt(i) >= word.charAt(i + 1)) {
                    return false;
                }
            }
            return true;
        };

        return Arrays.stream(input.trim().split("\\s+"))
                .filter(isAscending)
                .toArray(String[]::new);
    }

    // Demonstration
    public static void main(String[] args) {
        String input = "abc acb almost bity cat dgh ghij hi az";
        String[] output = findAscendingWords(input);

        System.out.println("Input: " + input);
        System.out.println("Ascending-order words: " + Arrays.toString(output));
    }
}
