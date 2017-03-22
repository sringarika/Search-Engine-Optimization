import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Flynn on 19/03/2017.
 */
public class DictionaryBuilder {
    class Pair {
        String word;
        int count;
        public Pair(String word, int count) {
            this.count = count;
            this.word = word;
        }
    }

    private final String tagRegex = "<([^>]*)>";
    private final String annotateRegex = "<!--.[^-]*(?=-->)-->";
    private final String stopWordsFile = "/Users/Flynn/Desktop/eBusiness/Task 10/stop words";
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
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            Set<String> set = new HashSet<>();
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
//                        if (!map.containsKey(part)) {
//                            map.put(part, 0);
//                        }
//                        map.put(part, map.get(part) + 1);


                        set.add(part);
                    }
                }
            }


            for (String s : set) {
                if (!map.containsKey(s)) {
                    map.put(s, 0);
                }
                map.put(s, map.get(s) + 1);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void buildDictionary(String filePath, String csvPath) {
        getStopWords();
        for (int i = 1; i <= 4544; i++) {
            getMap(filePath + i + ".html");
        }

        List<Pair> list = new ArrayList<>();
        for (String word : map.keySet()) {
            list.add(new Pair(word, map.get(word)));
        }

        Collections.sort(list, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return o2.count - o1.count;
            }
        });

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(csvPath));
            for (int i = 0; i < list.size(); i++) {
                out.write(list.get(i).word + "\t" + list.get(i).count);
                out.newLine();
            }
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
