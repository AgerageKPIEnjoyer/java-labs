import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

class SupervisorLogEntry {

    private final String surname;
    private final String firstName;
    private final LocalDate dateOfBirth;
    private final String phoneNumber;
    private final String street;
    private final String building;
    private final String apartment;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.uuuu");

    public SupervisorLogEntry(String surname,
                              String firstName,
                              LocalDate dateOfBirth,
                              String phoneNumber,
                              String street,
                              String building,
                              String apartment) {
        this.surname = surname;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.building = building;
        this.apartment = apartment;
    }

    @Override
    public String toString() {
        String address = "Street: " + street +
                ", Building: " + building +
                ", Apartment: " + (apartment.equals("-") ? "none" : apartment);

        return "Surname:        " + surname + "\n" +
                "First name:     " + firstName + "\n" +
                "Date of birth:  " + dateOfBirth.format(DATE_FORMAT) + "\n" +
                "Phone number:   " + phoneNumber + "\n" +
                "Home address:   " + address;
    }
}

class InputReader {

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-zА-Яа-яЁёІіЇїЄєҐґ]{2,30}(-[A-Za-zА-Яа-яЁёІіЇїЄєҐґ]{2,30})?$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?\\d{10,13}$");

    private static final Pattern STREET_PATTERN =
            Pattern.compile("^[A-Za-zА-Яа-яЁёІіЇїЄєҐґ0-9 .'-]{2,50}$");

    private static final Pattern BUILDING_PATTERN =
            Pattern.compile("^(?=.{1,10}$)\\d{1,4}[A-Za-zА-Яа-яЁёІіЇїЄєҐґ]?([/-]\\d{1,4}[A-Za-zА-Яа-яЁёІіЇїЄєҐґ]?)?$");

    private static final Pattern APARTMENT_PATTERN =
            Pattern.compile("^(-|[1-9][0-9]{0,3})$");

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Generic helper: keeps asking until the value matches the pattern. */
    private String readValidated(String prompt, Pattern pattern, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (pattern.matcher(value).matches()) {
                return value;
            }
            System.out.println("  Error: " + errorMessage + " Please try again.");
        }
    }

    public String readSurname() {
        return readValidated(
                "Enter student's surname: ",
                NAME_PATTERN,
                "surname must contain 2-30 letters only (hyphen allowed, e.g. \"Petrenko-Ivanov\").");
    }

    public String readFirstName() {
        return readValidated(
                "Enter student's first name: ",
                NAME_PATTERN,
                "first name must contain 2-30 letters only (hyphen allowed).");
    }

    public LocalDate readDateOfBirth() {
        while (true) {
            System.out.print("Enter student's date of birth (dd.MM.yyyy): ");
            String value = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(value, DATE_FORMAT);
                if (date.isAfter(LocalDate.now())) {
                    System.out.println("  Error: date of birth cannot be in the future. Please try again.");
                    continue;
                }
                if (date.isBefore(LocalDate.now().minusYears(100))) {
                    System.out.println("  Error: date of birth is unrealistically far in the past. Please try again.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("  Error: date must be in the format dd.MM.yyyy (e.g. 05.09.2007). Please try again.");
            }
        }
    }

    public String readPhoneNumber() {
        return readValidated(
                "Enter student's phone number (e.g. +380671234567): ",
                PHONE_PATTERN,
                "phone number must contain 10-13 digits, optionally starting with '+'.");
    }

    public String readStreet() {
        return readValidated(
                "Enter street name: ",
                STREET_PATTERN,
                "street name must be 2-50 characters (letters, digits, spaces, '.', '-' allowed).");
    }

    public String readBuilding() {
        return readValidated(
                "Enter building number: ",
                BUILDING_PATTERN,
                "building number must start with digits (1-4), e.g. \"12\", \"12A\", \"5/2\" or \"7-B\".");
    }

    public String readApartment() {
        return readValidated(
                "Enter apartment number (or '-' if none): ",
                APARTMENT_PATTERN,
                "apartment number must be a positive number, or '-' if there is none.");
    }
}

public class Main {

    private static final List<SupervisorLogEntry> LOG_ENTRIES = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InputReader reader = new InputReader(scanner);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addLogEntry(reader);
                    break;
                case "2":
                    showAllEntries();
                    break;
                case "0":
                    running = false;
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("Unknown option, please choose 1, 2 or 0.\n");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" SUPERVISOR'S LOG ENTRY - MAIN MENU");
        System.out.println("=========================================");
        System.out.println(" 1 - Add a new log entry");
        System.out.println(" 2 - Show all log entries");
        System.out.println(" 0 - Exit");
        System.out.print("Your choice: ");
    }

    /**
     * Reads all fields of an entry, validating each one; once every field
     * is correct, the data is passed to the SupervisorLogEntry model and
     * stored in the list.
     */
    private static void addLogEntry(InputReader reader) {
        System.out.println("\n--- New log entry ---");

        String surname = reader.readSurname();
        String firstName = reader.readFirstName();
        LocalDate dateOfBirth = reader.readDateOfBirth();
        String phoneNumber = reader.readPhoneNumber();
        String street = reader.readStreet();
        String building = reader.readBuilding();
        String apartment = reader.readApartment();

        // All fields are guaranteed valid at this point -> create the entity
        SupervisorLogEntry entry = new SupervisorLogEntry(
                surname, firstName, dateOfBirth, phoneNumber, street, building, apartment);

        LOG_ENTRIES.add(entry);
        System.out.println("\nEntry successfully added!\n");
    }

    private static void showAllEntries() {
        System.out.println("\n=========================================");
        System.out.println(" ALL LOG ENTRIES (" + LOG_ENTRIES.size() + ")");
        System.out.println("=========================================");

        if (LOG_ENTRIES.isEmpty()) {
            System.out.println("No entries have been created yet.\n");
            return;
        }

        for (int i = 0; i < LOG_ENTRIES.size(); i++) {
            System.out.println("Entry #" + (i + 1) + ":");
            System.out.println(LOG_ENTRIES.get(i));
            System.out.println("-----------------------------------------");
        }
        System.out.println();
    }
}