package view;

import java.util.Map;
import java.util.Scanner;

/**
 * Everything the user sees or types goes through this class. The
 * controller never calls System.out/Scanner directly - it only talks to
 * the view.
 */
public class TranslatorView {

    private final Scanner scanner = new Scanner(System.in);

    public void printMenu() {
        System.out.println("=========================================");
        System.out.println(" ENGLISH -> UKRAINIAN TRANSLATOR");
        System.out.println("=========================================");
        System.out.println(" 1 - Add a word pair to the dictionary");
        System.out.println(" 2 - Translate a phrase");
        System.out.println(" 3 - Show the dictionary");
        System.out.println(" 0 - Exit");
        System.out.print("Your choice: ");
    }

    public String readMenuChoice() {
        return scanner.nextLine().trim();
    }

    public String[] promptWordPair() {
        System.out.print("English word: ");
        String english = scanner.nextLine().trim();
        System.out.print("Ukrainian translation: ");
        String ukrainian = scanner.nextLine().trim();
        return new String[]{english, ukrainian};
    }

    public String promptPhrase() {
        System.out.print("Enter an English phrase to translate: ");
        return scanner.nextLine();
    }

    public void displayTranslation(String original, String translated) {
        System.out.println("Original   : " + original);
        System.out.println("Translation: " + translated + "\n");
    }

    public void displayDictionary(Map<String, String> dictionary) {
        System.out.println("\n--- Dictionary (" + dictionary.size() + " words) ---");
        dictionary.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %-15s -> %s%n", e.getKey(), e.getValue()));
        System.out.println();
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void close() {
        scanner.close();
    }
}
