package main.java.com.example.lab.counter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Finds the line containing the maximum number of words in a file.
 */
public final class WordCounterUtil {

    private WordCounterUtil() {
        // utility class
    }

    /**
     * @return the first line with the highest word count, or null if the file has no lines
     */
    public static String findLineWithMaxWords(Path file) throws IOException {
        String bestLine = null;
        int bestCount = -1;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int words = countWords(line);
                if (words > bestCount) {
                    bestCount = words;
                    bestLine = line;
                }
            }
        }
        return bestLine;
    }

    public static int countWords(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }
}
