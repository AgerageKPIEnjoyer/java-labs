package main.java.com.example.lab.html;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

/**
 * The dataset that gets saved/loaded via object streams. Deliberately has
 * no writeObject/readObject overrides, so serialization is entirely the
 * JVM's default mechanism, as required. Every field type here
 * (String, LocalDateTime, TreeMap<String,Integer>) is itself Serializable,
 * which is what makes default serialization sufficient.
 */
public class TagFrequencyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String sourceUrl;
    private final LocalDateTime generatedAt;
    private final Map<String, Integer> tagFrequencies;

    public TagFrequencyReport(String sourceUrl, Map<String, Integer> tagFrequencies) {
        this.sourceUrl = sourceUrl;
        this.generatedAt = LocalDateTime.now();
        this.tagFrequencies = new TreeMap<>(tagFrequencies);
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public Map<String, Integer> getTagFrequencies() {
        return tagFrequencies;
    }

    @Override
    public String toString() {
        return "TagFrequencyReport{url=" + sourceUrl + ", generatedAt=" + generatedAt +
                ", distinctTags=" + tagFrequencies.size() + "}";
    }
}
