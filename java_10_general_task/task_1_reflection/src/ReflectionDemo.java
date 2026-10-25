import java.lang.reflect.InaccessibleObjectException;
import java.util.Scanner;

public class ReflectionDemo {

    public static void main(String[] args) {
        StringMutator mutator;
        try {
            mutator = new StringMutator();
        } catch (InaccessibleObjectException e) {
            System.out.println("Access to java.lang.String is blocked by the module system.");
            System.out.println("Run the program with this VM option:");
            System.out.println("    --add-opens java.base/java.lang=ALL-UNNAMED");
            return;
        } catch (ReflectiveOperationException e) {
            System.out.println("Cannot find the internal fields of String: " + e);
            return;
        }
        System.out.println("Internal array of String on this JVM: " + mutator.valueFieldType() + " value");

        try (Scanner scanner = new Scanner(System.in)) {
            fromLiteral(mutator);
            fromKeyboard(mutator, scanner);
        } catch (ReflectiveOperationException e) {
            System.out.println("Modification failed: " + e);
        }
    }

    /** Case 1: the string is created as a literal, the replacement is defined in code. */
    private static void fromLiteral(StringMutator mutator) throws ReflectiveOperationException {
        System.out.println();
        System.out.println("=== Case 1: string literal ===");
        String text = "Original literal text";
        String sameLiteral = "Original literal text"; // the compiler reuses ONE pooled object for equal literals

        System.out.println("Before: text        = \"" + text + "\"  (hashCode " + text.hashCode() + ")");
        System.out.println("Before: sameLiteral = \"" + sameLiteral + "\"  (same object: " + (text == sameLiteral) + ")");

        mutator.replace(text, "Literal changed via reflection!");

        System.out.println("After:  text        = \"" + text + "\"  (hashCode " + text.hashCode() + ")");
        System.out.println("After:  sameLiteral = \"" + sameLiteral + "\"");
        System.out.print("After:  the literal written again in code now gives: ");
        System.out.println("Original literal text");
        System.out.println("(a literal is shared through the string pool, so every use of it changed)");
    }

    /** Case 2: the string is entered on the keyboard, the replacement is typed as well. */
    private static void fromKeyboard(StringMutator mutator, Scanner scanner) throws ReflectiveOperationException {
        System.out.println();
        System.out.println("=== Case 2: keyboard input ===");
        System.out.print("Enter a string: ");
        String text = scanner.nextLine();
        System.out.print("Enter the replacement value: ");
        String replacement = scanner.nextLine();

        System.out.println("Before: \"" + text + "\"  (length " + text.length() + ", hashCode " + text.hashCode() + ")");
        mutator.replace(text, replacement);
        System.out.println("After:  \"" + text + "\"  (length " + text.length() + ", hashCode " + text.hashCode() + ")");
        System.out.println("Sanity check, equals(replacement): " + text.equals(replacement));
    }
}
