package localized.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configures java.util.logging for the whole application.
 *
 * The handlers are attached to the root logger ("") - the ancestor of every logger,
 * so the configuration applies no matter which package a class belongs to.
 *
 * Three filters stand between a message and its destination:
 *   1. the logger level (FINE)   - FINER and FINEST are dropped immediately;
 *   2. the console handler level (WARNING by default) - only warnings and errors are shown;
 *   3. the file handler level (FINE) - everything the logger let through is written.
 */
public final class AppLogging {

    public static final Level LOGGER_LEVEL = Level.FINE;
    public static final Level FILE_LEVEL = Level.FINE;

    private static final Logger ROOT = Logger.getLogger("");

    private static ConsoleHandler console;
    private static FileHandler file;

    private AppLogging() {
    }

    public static void init(Path logFile) throws IOException {
        for (Handler handler : ROOT.getHandlers()) {
            ROOT.removeHandler(handler); // drops the default console handler (timestamped, INFO and above)
        }
        ROOT.setLevel(LOGGER_LEVEL);

        console = new ConsoleHandler(); // writes to System.err
        console.setLevel(Level.WARNING);
        console.setFormatter(new LogFormatter(false));
        ROOT.addHandler(console);

        Path dir = logFile.toAbsolutePath().getParent();
        if (dir != null) {
            Files.createDirectories(dir);
        }
        file = new FileHandler(logFile.toString().replace("%", "%%"), 0, 1, true); // append
        file.setEncoding("UTF-8");
        file.setLevel(FILE_LEVEL);
        file.setFormatter(new LogFormatter(true));
        ROOT.addHandler(file);
    }

    public static Level consoleLevel() {
        return console.getLevel();
    }

    public static void setConsoleLevel(Level level) {
        console.setLevel(level);
    }

    public static void shutdown() {
        for (Handler handler : ROOT.getHandlers()) {
            handler.flush();
            handler.close();
            ROOT.removeHandler(handler);
        }
    }
}