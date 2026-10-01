package main.java.com.example.lab.html;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Downloads an HTML page and computes how many times each HTML
 * tag name occurs (counting opening tags only, so &lt;div&gt; and
 * &lt;/div&gt; both count toward "div" - but only the opening one is
 * matched, avoiding double counting).
 */
public class TagFrequencyAnalyzer {

    // Matches "<tagname" but not "</tagname", "<!--", or "<!DOCTYPE",
    // since the character right after '<' must be a letter.
    private static final Pattern TAG_PATTERN = Pattern.compile("<\\s*([a-zA-Z][a-zA-Z0-9]*)");

    public String fetchHtml(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Unexpected HTTP status " + response.statusCode() + " for " + url);
        }
        return response.body();
    }

    public Map<String, Integer> countTags(String html) {
        Map<String, Integer> counts = new HashMap<>();
        Matcher matcher = TAG_PATTERN.matcher(html);
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();
            counts.merge(tag, 1, Integer::sum);
        }
        return counts;
    }

    public List<Map.Entry<String, Integer>> sortByNameAscending(Map<String, Integer> counts) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort(Map.Entry.comparingByKey());
        return list;
    }

    public List<Map.Entry<String, Integer>> sortByFrequencyAscending(Map<String, Integer> counts) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort(Map.Entry.comparingByValue());
        return list;
    }
}
