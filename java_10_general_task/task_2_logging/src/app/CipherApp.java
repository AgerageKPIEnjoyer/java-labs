package app;

import cipher.CipherFileService;
import logging.AppLogging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CipherApp {

    private static final Logger LOG = Logger.getLogger(CipherApp.class.getName());
    private static final Path LOG_FILE = Path.of("logs", "cipher-app.log");
    private static final List<Level> SELECTABLE_LEVELS =
            List.of(Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE);
    private static final List<Level> ALL_LEVELS = List.of(
            Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST);

    private final CipherFileService service = new CipherFileService();
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        AppLogging.init(LOG_FILE);
        try {
            new CipherApp().run();
        } finally {
            AppLogging.shutdown();
        }
    }

    private void run() throws IOException {
        LOG.info("Application started");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = ask("Your choice");
            if (choice == null) {
                LOG.warning("End of input reached, leaving the program");
                break;
            }
            LOG.fine(() -> "Menu choice: '" + choice.trim() + "'");
            switch (choice.trim()) {
                case "1" -> process(true);
                case "2" -> process(false);
                case "3" -> chooseConsoleLevel();
                case "4" -> demonstrateLevels();
                case "0" -> running = false;
                default -> LOG.warning("Unknown menu option: '" + choice.trim() + "'");
            }
        }
        LOG.info("Application finished");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Cipher tool ===");
        System.out.println("1. Encrypt a file");
        System.out.println("2. Decrypt a file");
        System.out.println("3. Change console log level (now " + AppLogging.consoleLevel() + ")");
        System.out.println("4. Demonstrate log levels");
        System.out.println("0. Exit");
    }

    private void process(boolean encrypt) throws IOException {
        String source = ask("Path of the input file");
        String target = ask("Path of the output file");
        if (source == null || target == null) {
            return;
        }
        Character key = askKey();
        if (key == null) {
            return;
        }
        try {
            long count = encrypt
                    ? service.encryptFile(Path.of(source.trim()), Path.of(target.trim()), key)
                    : service.decryptFile(Path.of(source.trim()), Path.of(target.trim()), key);
            System.out.println((encrypt ? "Encrypted " : "Decrypted ") + count + " characters into " + target.trim());
        } catch (IOException e) {
            // already logged (SEVERE) by the service; only tell the user that it did not work
            System.out.println("The operation failed, see the log for details.");
        }
    }

    private Character askKey() throws IOException {
        while (true) {
            String line = ask("Key character (only the first character is used)");
            if (line == null) {
                return null;
            }
            if (line.isEmpty()) {
                LOG.warning("The key must not be empty");
                continue;
            }
            if (line.length() > 1) {
                LOG.info("Key is longer than one character, using '" + line.charAt(0) + "'");
            }
            return line.charAt(0);
        }
    }

    private void chooseConsoleLevel() throws IOException {
        System.out.println("Console shows messages of the chosen level and above:");
        for (int i = 0; i < SELECTABLE_LEVELS.size(); i++) {
            System.out.println((i + 1) + ". " + SELECTABLE_LEVELS.get(i));
        }
        String answer = ask("Level number");
        try {
            int index = Integer.parseInt(answer.trim()) - 1;
            AppLogging.setConsoleLevel(SELECTABLE_LEVELS.get(index));
            LOG.info("Console log level changed to " + SELECTABLE_LEVELS.get(index));
            System.out.println("Console log level is now " + AppLogging.consoleLevel());
        } catch (RuntimeException e) {
            LOG.warning("Invalid level selection: '" + answer + "'");
        }
    }

    /** Logs one message per level and prints which destinations accepted it. */
    private void demonstrateLevels() {
        for (Level level : ALL_LEVELS) {
            LOG.log(level, "Demo message with level " + level.getName());
        }
        System.out.println();
        System.out.printf("%-8s %-8s %-9s %-6s%n", "Level", "Logger", "Console", "File");
        for (Level level : ALL_LEVELS) {
            boolean logger = level.intValue() >= AppLogging.LOGGER_LEVEL.intValue();
            boolean console = logger && level.intValue() >= AppLogging.consoleLevel().intValue();
            boolean file = logger && level.intValue() >= AppLogging.FILE_LEVEL.intValue();
            System.out.printf("%-8s %-8s %-9s %-6s%n", level.getName(), yesNo(logger), yesNo(console), yesNo(file));
        }
        System.out.println("Logger = passed the logger level; messages rejected there reach no destination.");
        System.out.println("The file is " + LOG_FILE.toAbsolutePath());
    }

    private static String yesNo(boolean value) {
        return value ? "yes" : "-";
    }

    /** @return the typed line, or null when the input has ended */
    private String ask(String prompt) throws IOException {
        System.out.print(prompt + ": ");
        System.out.flush();
        return input.readLine();
    }
}
