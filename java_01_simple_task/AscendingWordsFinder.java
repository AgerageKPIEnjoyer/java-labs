import java.util.ArrayList;
import java.util.List;

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

        String[] words = input.trim().split("\\s+");
        List<String> result = new ArrayList<>();

        for (String word : words) {
            if (isAscending(word)) {
                result.add(word);
            }
        }

        return result.toArray(new String[0]);
    }

    /**
     * Checks whether the characters of a word appear in strictly
     * ascending order of character code.
     */
    private static boolean isAscending(String word) {
        for (int i = 0; i < word.length() - 1; i++) {
            if (word.charAt(i) >= word.charAt(i + 1)) {
                return false;
            }
        }
        return true;
    }

    // Demonstration
    public static void main(String[] args) {
        String input = "abc acb almost bity cat dgh ghij hi az";
        String[] output = findAscendingWords(input);

        System.out.println("Input: " + input);
        System.out.print("Ascending-order words: [");
        for (int i = 0; i < output.length; i++) {
            System.out.print(output[i]);
            if (i < output.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }
}