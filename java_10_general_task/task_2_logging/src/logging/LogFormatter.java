package logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Two layouts: a short one for the console ("[WARNING] text") and a detailed
 * one for the file ("time | level | Class.method | text", with stack traces).
 */
public class LogFormatter extends Formatter {

    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private final boolean detailed;

    public LogFormatter(boolean detailed) {
        this.detailed = detailed;
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        if (detailed) {
            sb.append(TIME.format(record.getInstant())).append(" | ")
              .append(String.format("%-7s", record.getLevel().getName())).append(" | ")
              .append(String.format("%-34s", source(record))).append(" | ");
        } else {
            sb.append('[').append(record.getLevel().getName()).append("] ");
        }
        sb.append(formatMessage(record)).append(System.lineSeparator());

        Throwable thrown = record.getThrown();
        if (thrown != null && detailed) {
            StringWriter trace = new StringWriter();
            thrown.printStackTrace(new PrintWriter(trace));
            sb.append(trace);
        }
        return sb.toString();
    }

    private static String source(LogRecord record) {
        String cls = record.getSourceClassName();
        if (cls == null) {
            return String.valueOf(record.getLoggerName());
        }
        String simple = cls.substring(cls.lastIndexOf('.') + 1);
        return record.getSourceMethodName() == null ? simple : simple + "." + record.getSourceMethodName();
    }
}
