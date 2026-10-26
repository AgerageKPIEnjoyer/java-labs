package localized.app;

import localized.cipher.CipherFileService;
import localized.i18n.Messages;
import localized.logging.AppLogging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Menu application from Task 10.2 with a "Language selection" item.
 * Everything the user reads is taken from the resource bundle; the log
 * messages deliberately stay in English (they are written for developers).
 */
public class LocalizedCipherApp {

    private static final Logger LOG = Logger.getLogger(LocalizedCipherApp.class.getName());
    private static final Path LOG_FILE = Path.of("logs", "cipher-app-i18n.log");
    private static final List<Level> SELECTABLE_LEVELS =
            List.of(Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE);
    private static final List<Level> ALL_LEVELS = List.of(
            Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST);

    private final CipherFileService service = new CipherFileService();
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private final Messages messages = new Messages(Locale.ENGLISH);

    public static void main(String[] args) throws IOException {
        AppLogging.init(LOG_FILE);
        try {
            new LocalizedCipherApp().run();
        } finally {
            AppLogging.shutdown();
        }
    }

    private void run() throws IOException {
        LOG.info("Application started");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = ask("prompt.choice");
            if (choice == null) {
                LOG.warning("End of input reached, leaving the program");
                break;
            }
            LOG.fine(() -> "Menu choice: '" + choice.trim() + "'");
            switch (choice.trim()) {
                case "1" -> process(true);
                case "2" -> process(false);
                case "3" -> chooseLanguage();
                case "4" -> chooseConsoleLevel();
                case "5" -> demonstrateLevels();
                case "0" -> running = false;
                default -> {
                    LOG.warning("Unknown menu option: '" + choice.trim() + "'");
                    System.out.println(messages.get("error.invalid.choice", choice.trim()));
                }
            }
        }
        System.out.println(messages.get("bye"));
        LOG.info("Application finished");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== " + messages.get("app.title") + " ===");
        System.out.println(messages.get("menu.header"));
        System.out.println("1. " + messages.get("menu.encrypt"));
        System.out.println("2. " + messages.get("menu.decrypt"));
        System.out.println("3. " + messages.get("menu.language"));
        System.out.println("4. " + messages.get("menu.loglevel", AppLogging.consoleLevel().getName()));
        System.out.println("5. " + messages.get("menu.logdemo"));
        System.out.println("0. " + messages.get("menu.exit"));
    }

    private void chooseLanguage() throws IOException {
        System.out.println(messages.get("language.header"));
        List<Locale> languages = Messages.SUPPORTED;
        for (int i = 0; i < languages.size(); i++) {
            System.out.println((i + 1) + ". " + Messages.languageName(languages.get(i)));
        }
        String answer = ask("prompt.language");
        try {
            Locale chosen = languages.get(Integer.parseInt(answer.trim()) - 1);
            messages.setLocale(chosen);
            System.out.println(messages.get("language.changed", Messages.languageName(chosen)));
        } catch (RuntimeException e) {
            LOG.warning("Invalid language selection: '" + answer + "'");
            System.out.println(messages.get("error.invalid.language", answer));
        }
    }

    private void process(boolean encrypt) throws IOException {
        String source = ask("prompt.source");
        String target = ask("prompt.target");
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
            System.out.println(messages.get(encrypt ? "info.encrypted" : "info.decrypted",
                    String.valueOf(count), target.trim()));
        } catch (IOException e) {
            // the technical details were already logged by the service
            System.out.println(messages.get("error.operation", e.toString()));
        }
    }

    private Character askKey() throws IOException {
        while (true) {
            String line = ask("prompt.key");
            if (line == null) {
                return null;
            }
            if (line.isEmpty()) {
                LOG.warning("The key must not be empty");
                System.out.println(messages.get("error.empty.key"));
                continue;
            }
            if (line.length() > 1) {
                LOG.info("Key is longer than one character, using '" + line.charAt(0) + "'");
            }
            return line.charAt(0);
        }
    }

    private void chooseConsoleLevel() throws IOException {
        System.out.println(messages.get("loglevel.header"));
        for (int i = 0; i < SELECTABLE_LEVELS.size(); i++) {
            System.out.println((i + 1) + ". " + SELECTABLE_LEVELS.get(i));
        }
        String answer = ask("prompt.level");
        try {
            Level level = SELECTABLE_LEVELS.get(Integer.parseInt(answer.trim()) - 1);
            AppLogging.setConsoleLevel(level);
            LOG.info("Console log level changed to " + level);
            System.out.println(messages.get("loglevel.changed", level.getName()));
        } catch (RuntimeException e) {
            LOG.warning("Invalid level selection: '" + answer + "'");
            System.out.println(messages.get("error.invalid.level", answer));
        }
    }

    /** Logs one message per level and prints which destinations accepted it. */
    private void demonstrateLevels() {
        for (Level level : ALL_LEVELS) {
            LOG.log(level, "Demo message with level " + level.getName());
        }
        System.out.println();
        System.out.printf("%-8s %-8s %-9s %-6s%n", messages.get("logdemo.col.level"),
                messages.get("logdemo.col.logger"), messages.get("logdemo.col.console"),
                messages.get("logdemo.col.file"));
        for (Level level : ALL_LEVELS) {
            boolean logger = level.intValue() >= AppLogging.LOGGER_LEVEL.intValue();
            boolean console = logger && level.intValue() >= AppLogging.consoleLevel().intValue();
            boolean file = logger && level.intValue() >= AppLogging.FILE_LEVEL.intValue();
            System.out.printf("%-8s %-8s %-9s %-6s%n", level.getName(), yesNo(logger), yesNo(console), yesNo(file));
        }
        System.out.println(messages.get("logdemo.note"));
        System.out.println(messages.get("logdemo.file", LOG_FILE.toAbsolutePath().toString()));
    }

    private String yesNo(boolean value) {
        return messages.get(value ? "logdemo.yes" : "logdemo.no");
    }

    /** @return the typed line, or null when the input has ended */
    private String ask(String promptKey) throws IOException {
        System.out.print(messages.get(promptKey) + ": ");
        System.out.flush();
        return input.readLine();
    }
}
