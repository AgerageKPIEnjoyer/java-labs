package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An English-to-Ukrainian translator: holds a word dictionary and can
 * translate a phrase word by word. Pure domain logic - knows nothing
 * about the console, menus, or how words get typed in.
 */
public class Translator {

    private final Map<String, String> dictionary = new HashMap<>();

    /** Seeds the dictionary with a handful of common words. */
    public Translator() {
        addWord("hello", "привіт");
        addWord("world", "світ");
        addWord("good", "добрий");
        addWord("morning", "ранок");
        addWord("friend", "друг");
        addWord("book", "книга");
        addWord("water", "вода");
        addWord("house", "дім");
        addWord("yes", "так");
        addWord("no", "ні");
    }

    /**
     * Adds (or overwrites) a word pair. Stored in lowercase so lookups
     * are case-insensitive regardless of how the phrase is capitalized.
     */
    public void addWord(String english, String ukrainian) {
        dictionary.put(english.toLowerCase(), ukrainian);
    }

    public boolean containsWord(String english) {
        return dictionary.containsKey(english.toLowerCase());
    }

    public int size() {
        return dictionary.size();
    }

    /** Read-only view of the dictionary, for display purposes. */
    public Map<String, String> getDictionary() {
        return Collections.unmodifiableMap(dictionary);
    }

    /**
     * Translates a phrase word by word. Punctuation attached to a word
     * (e.g. "world," or "friend!") is kept but doesn't prevent a match -
     * only letters/digits are used for the dictionary lookup. A word with
     * no dictionary entry is left as-is, wrapped in brackets so the gap
     * is visible, e.g. "[subject]".
     */
    public String translate(String phrase) {
        if (phrase == null || phrase.isBlank()) {
            return "";
        }

        String[] tokens = phrase.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) {
                result.append(' ');
            }
            result.append(translateToken(tokens[i]));
        }
        return result.toString();
    }

    private String translateToken(String token) {
        String core = token.replaceAll("[^\\p{L}\\p{N}]", "");
        if (core.isEmpty()) {
            return token; // pure punctuation, e.g. "--"
        }

        String translation = dictionary.get(core.toLowerCase());
        if (translation == null) {
            return "[" + token + "]";
        }

        // Reattach any leading/trailing punctuation that surrounded the word.
        int start = token.toLowerCase().indexOf(core.toLowerCase());
        String prefix = token.substring(0, start);
        String suffix = token.substring(start + core.length());
        return prefix + translation + suffix;
    }
}
