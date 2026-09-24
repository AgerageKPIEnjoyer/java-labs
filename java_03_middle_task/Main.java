import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

/**
 * Console entry point: a menu-driven simulation of the card registry
 * ("system") and the turnstile that is linked to it.
 */
public class Main {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ski season for this simulation: 1 Dec this/last year -> 30 Apr next.
        LocalDate seasonEnd = LocalDate.now().getMonthValue() >= 8
                ? LocalDate.of(LocalDate.now().getYear() + 1, 4, 30)
                : LocalDate.of(LocalDate.now().getYear(), 4, 30);

        CardRegistry registry = new CardRegistry(seasonEnd);
        Turnstile turnstile = new Turnstile(registry);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    issuePass(scanner, registry);
                    break;
                case "2":
                    blockPass(scanner, registry);
                    break;
                case "3":
                    simulateEntry(scanner, turnstile);
                    break;
                case "4":
                    turnstile.getStatistics().printAggregateReport();
                    break;
                case "5":
                    turnstile.getStatistics().printByTypeReport();
                    break;
                case "6":
                    listCards(registry);
                    break;
                case "0":
                    running = false;
                    System.out.println("Shutting down. Goodbye!");
                    break;
                default:
                    System.out.println("Unknown option.\n");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" SKI LIFT TURNSTILE SIMULATION");
        System.out.println("=========================================");
        System.out.println(" 1 - Issue a new ski pass");
        System.out.println(" 2 - Block a ski pass");
        System.out.println(" 3 - Simulate a turnstile entry attempt");
        System.out.println(" 4 - Show aggregate access statistics");
        System.out.println(" 5 - Show access statistics by pass type");
        System.out.println(" 6 - List all issued cards");
        System.out.println(" 0 - Exit");
        System.out.print("Your choice: ");
    }

    // ---------------------------------------------------------------
    // 1. Issue a pass
    // ---------------------------------------------------------------
    private static void issuePass(Scanner scanner, CardRegistry registry) {
        System.out.println("\nAvailable pass types:");
        PassType[] types = PassType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("  %2d - %s%n", i + 1, types[i].getDisplayName());
        }

        PassType chosen = readMenuChoice(scanner, "Select pass type number: ", types);

        while (true) {
            LocalDate startDate = readDate(scanner, "Enter start date (dd.MM.yyyy): ");
            try {
                SkiPassCard card = registry.issuePass(chosen, startDate);
                System.out.println("Pass issued: " + card + "\n");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("  Error: " + e.getMessage() + "\n");
            }
        }
    }

    // ---------------------------------------------------------------
    // 2. Block a pass
    // ---------------------------------------------------------------
    private static void blockPass(Scanner scanner, CardRegistry registry) {
        System.out.print("\nEnter card id to block (full id or just the number, e.g. 7): ");
        String id = scanner.nextLine().trim();
        boolean ok = registry.blockPass(id);
        if (ok) {
            System.out.println("Card " + id + " has been blocked.\n");
        } else {
            System.out.println("No card found with id " + id + ".\n");
        }
    }

    // ---------------------------------------------------------------
    // 3. Simulate an entry attempt
    // ---------------------------------------------------------------
    private static void simulateEntry(Scanner scanner, Turnstile turnstile) {
        System.out.print("\nEnter card id presented at the turnstile (full id or just the number, e.g. 7): ");
        String id = scanner.nextLine().trim();

        System.out.print("Simulate a card read failure for this attempt? (y/N): ");
        boolean forceUnreadable = scanner.nextLine().trim().equalsIgnoreCase("y");

        while (true) {
            System.out.print("Enter date/time of the attempt (dd.MM.yyyy HH:mm), or leave blank for now: ");
            String dtInput = scanner.nextLine().trim();
            LocalDateTime moment = dtInput.isEmpty() ? LocalDateTime.now() : parseDateTimeLoop(scanner, dtInput);

            try {
                AccessResult result = turnstile.attemptEntry(id, moment, forceUnreadable);
                System.out.println("Result: " + result + " - " + result.getDescription() + "\n");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("  Error: " + e.getMessage() + "\n");
            }
        }
    }

    private static LocalDateTime parseDateTimeLoop(Scanner scanner, String firstAttempt) {
        String value = firstAttempt;
        while (true) {
            try {
                return LocalDateTime.parse(value, DATE_TIME_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.print("  Error: expected format dd.MM.yyyy HH:mm. Try again: ");
                value = scanner.nextLine().trim();
            }
        }
    }

    // ---------------------------------------------------------------
    // 6. List cards
    // ---------------------------------------------------------------
    private static void listCards(CardRegistry registry) {
        System.out.println("\n=== Issued cards (" + registry.getAllCards().size() + ") ===");
        if (registry.getAllCards().isEmpty()) {
            System.out.println("No cards issued yet.\n");
            return;
        }
        for (SkiPassCard card : registry.getAllCards().values()) {
            System.out.println(card);
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Small validated-input helpers
    // ---------------------------------------------------------------
    private static PassType readMenuChoice(Scanner scanner, String prompt, PassType[] options) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 1 && idx <= options.length) {
                    return options[idx - 1];
                }
            } catch (NumberFormatException ignored) {
                // fall through to error message below
            }
            System.out.println("  Error: enter a number between 1 and " + options.length + ".");
        }
    }

    private static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return LocalDate.parse(value, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("  Error: date must be in the format dd.MM.yyyy. Please try again.");
            }
        }
    }
}
