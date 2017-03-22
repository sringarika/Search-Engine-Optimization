import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Flynn on 19/03/2017.
 */
public class TermFrequency {
    private final String tagRegex = "<([^>]*)>";
    private final String annotateRegex = "<!--.[^-]*(?=-->)-->";
    private final String stopWordsFile = "/Users/Flynn/Desktop/eBusiness/Task 10/stop";
    private Map<String, Integer> map = new HashMap<>();
    private Set<String> stopWords = new HashSet<>();

    private String processLine(String str) {
        str = str.replaceAll(tagRegex, "");
        str = str.replaceAll(annotateRegex, "");
        str = str.replaceAll("&ldquo;", "“");
        str = str.replaceAll("&rdquo;", "”");
        str = str.replaceAll("&nbsp;", " ");
        str = str.replaceAll("&amp;", "&");
        str = str.replaceAll("&#39;", "'");
        str = str.replaceAll("&rsquo;", "’");
        str = str.replaceAll("&mdash;", "—");
        str = str.replaceAll("&ndash;", "–");
        str = str.replaceAll("&reg;", "");
        return str.trim();
    }

    private void getStopWords() {
        try {
            FileInputStream fis = new FileInputStream(stopWordsFile);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                stopWords.add(line.trim());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void getMap(String filePath) {
        getStopWords();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                line = processLine(line);
                if (line.length() > 0) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        part = part.toLowerCase();
                        if (stopWords.contains(part)) {
                            continue;
                        }
                        boolean allChar = true;
                        for (int i = 0; i < part.length(); i++) {
                            if ((part.charAt(i) >= 'a' && part.charAt(i) <= 'z') || (part.charAt(i) >= 'A' && part.charAt(i) <= 'Z')) {
                                continue;
                            } else {
                                allChar = false;
                            }
                        }
                        if (!allChar) {
                            continue;
                        }
                        if (!map.containsKey(part)) {
                            map.put(part, 0);
                        }
                        map.put(part, map.get(part) + 1);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public double calculate(String filePath, String word) {
        getMap(filePath);
        double total = 0;
        for (String s : map.keySet()) {
            total += map.get(s);
        }

        if (map.containsKey(word)) {
            return map.get(word) / total;
        }
        return 0;
    }
}
