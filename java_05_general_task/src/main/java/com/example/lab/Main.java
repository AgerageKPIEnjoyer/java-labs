package main.java.com.example.lab;

import main.java.com.example.lab.cipher.CipherFileService;
import main.java.com.example.lab.counter.WordCounterUtil;
import main.java.com.example.lab.exception.TagNotFoundException;
import main.java.com.example.lab.html.ReportFileManager;
import main.java.com.example.lab.html.TagFrequencyAnalyzer;
import main.java.com.example.lab.html.TagFrequencyReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final String TARGET_URL = "https://ageragekpienjoyer.github.io/KPI4-UI-lab/";

    private static final CipherFileService cipherService = new CipherFileService();
    private static final TagFrequencyAnalyzer tagAnalyzer = new TagFrequencyAnalyzer();
    private static final ReportFileManager reportFileManager = new ReportFileManager();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    findLineWithMaxWords(scanner);
                    break;
                case "2":
                    encryptFile(scanner);
                    break;
                case "3":
                    decryptFile(scanner);
                    break;
                case "4":
                    analyzeTagFrequency(scanner);
                    break;
                case "5":
                    searchSavedReport(scanner);
                    break;
                case "0":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Unknown option.\n");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" FILE HANDLING SYSTEM");
        System.out.println("=========================================");
        System.out.println(" 1 - Find the line with the maximum number of words in a file");
        System.out.println(" 2 - Encrypt a file");
        System.out.println(" 3 - Decrypt a file");
        System.out.println(" 4 - Analyze tag frequency of " + TARGET_URL);
        System.out.println(" 5 - Load a saved tag-frequency report and search it");
        System.out.println(" 0 - Exit");
        System.out.print("Your choice: ");
    }

    private static void findLineWithMaxWords(Scanner scanner) {
        Path file = readExistingFilePath(scanner, "\nEnter the path of the file to scan: ");
        try {
            String result = WordCounterUtil.findLineWithMaxWords(file);
            if (result == null) {
                System.out.println("The file is empty.\n");
            } else {
                System.out.println("Line with the most words (" + WordCounterUtil.countWords(result) + " words):");
                System.out.println("  " + result + "\n");
            }
        } catch (IOException e) {
            System.out.println("  Error reading file: " + e.getMessage() + "\n");
        }
    }

    private static void encryptFile(Scanner scanner) {
        Path input = readExistingFilePath(scanner, "\nEnter the path of the plain-text file to encrypt: ");
        Path output = readOutputFilePath(scanner, "Enter the path to save the encrypted file to: ");
        char key = readKeyChar(scanner);
        try {
            cipherService.encryptFile(input, output, key);
            System.out.println("Encrypted file written to " + output + "\n");
        } catch (IOException e) {
            System.out.println("  Error during encryption: " + e.getMessage() + "\n");
        }
    }

    private static void decryptFile(Scanner scanner) {
        Path input = readExistingFilePath(scanner, "\nEnter the path of the encrypted file: ");
        Path output = readOutputFilePath(scanner, "Enter the path to save the decrypted file to: ");
        char key = readKeyChar(scanner);
        try {
            cipherService.decryptFile(input, output, key);
            System.out.println("Decrypted file written to " + output + "\n");
        } catch (IOException e) {
            System.out.println("  Error during decryption: " + e.getMessage() + "\n");
        }
    }

    private static void analyzeTagFrequency(Scanner scanner) {
        String html;
        try {
            System.out.println("\nFetching " + TARGET_URL + " ...");
            html = tagAnalyzer.fetchHtml(TARGET_URL);
        } catch (IOException | InterruptedException e) {
            System.out.println("  Error fetching the page: " + e.getMessage() + "\n");
            return;
        }

        Map<String, Integer> counts = tagAnalyzer.countTags(html);
        if (counts.isEmpty()) {
            System.out.println("No tags found on the page.\n");
            return;
        }

        System.out.println("\n-- a) Sorted by tag name (lexicographical) --");
        for (Map.Entry<String, Integer> entry : tagAnalyzer.sortByNameAscending(counts)) {
            System.out.printf("  %-15s %d%n", entry.getKey(), entry.getValue());
        }

        System.out.println("\n-- b) Sorted by frequency (ascending) --");
        for (Map.Entry<String, Integer> entry : tagAnalyzer.sortByFrequencyAscending(counts)) {
            System.out.printf("  %-15s %d%n", entry.getKey(), entry.getValue());
        }
        System.out.println();

        System.out.print("Save this report to a file? (y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            Path destination = readOutputFilePath(scanner, "Enter the path to save the report to: ");
            try {
                TagFrequencyReport report = new TagFrequencyReport(TARGET_URL, counts);
                reportFileManager.saveReport(report, destination);
                System.out.println("Report saved to " + destination + "\n");
            } catch (IOException e) {
                System.out.println("  Error saving report: " + e.getMessage() + "\n");
            }
        }
    }

    private static void searchSavedReport(Scanner scanner) {
        Path source = readExistingFilePath(scanner, "\nEnter the path of a saved report file: ");

        TagFrequencyReport report;
        try {
            report = reportFileManager.loadReport(source);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("  Error loading report: " + e.getMessage() + "\n");
            return;
        }

        System.out.println("Loaded: " + report);
        System.out.println("Enter a tag name to search (or leave blank to return to the menu).");

        while (true) {
            System.out.print("Tag name: ");
            String tagName = scanner.nextLine().trim().toLowerCase();
            if (tagName.isEmpty()) {
                System.out.println();
                return;
            }
            try {
                int frequency = lookUpTag(report, tagName);
                System.out.println("  <" + tagName + "> occurs " + frequency + " time(s).\n");
            } catch (TagNotFoundException e) {
                System.out.println("  " + e.getMessage());
            }
        }
    }

    private static int lookUpTag(TagFrequencyReport report, String tagName) throws TagNotFoundException {
        Integer count = report.getTagFrequencies().get(tagName);
        if (count == null) {
            throw new TagNotFoundException("Tag \"" + tagName + "\" was not found in this dataset.");
        }
        return count;
    }

    // -----------------------------------------------------------
    // Validated input helpers
    // -----------------------------------------------------------
    private static Path readExistingFilePath(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                Path path = Paths.get(input);
                if (!Files.exists(path)) {
                    throw new IOException("no such file: " + input);
                }
                if (!Files.isRegularFile(path)) {
                    throw new IOException(input + " is not a regular file.");
                }
                return path;
            } catch (InvalidPathException | IOException e) {
                System.out.println("  Error: " + e.getMessage() + "\nPlease try again.");
            }
        }
    }

    private static Path readOutputFilePath(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Paths.get(input);
            } catch (InvalidPathException e) {
                System.out.println("  Error: " + e.getMessage() + " Please try again.");
            }
        }
    }

    private static char readKeyChar(Scanner scanner) {
        while (true) {
            System.out.print("Enter a single-character encryption key: ");
            String input = scanner.nextLine();
            try {
                if (input.length() != 1) {
                    throw new IllegalArgumentException("Key must be exactly one character.");
                }
                return input.charAt(0);
            } catch (IllegalArgumentException e) {
                System.out.println("  Error: " + e.getMessage());
            }
        }
    }
}
