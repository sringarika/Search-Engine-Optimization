import com.sun.xml.internal.bind.v2.model.core.ID;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by Flynn on 21/03/2017.
 */
public class Cosine {
    class Pair {
        String name;
        double cosine;
        public Pair( String name, double cosine) {
            this.name = name;
            this.cosine = cosine;
        }
    }

    private final String tagRegex = "<([^>]*)>";
    private final String annotateRegex = "<!--.[^-]*(?=-->)-->";
    private final String stopWordsFile = "/Users/Flynn/Desktop/eBusiness/Task 10/stop";
    private final String rootPath = "/Applications/IndriSEO/IndriSEO/data/";
    private final String IDF = "/Users/Flynn/Desktop/eBusiness/Task 10/IDF.csv";
    private Set<String> stopwords = new HashSet<>();
    private Map<String, Integer> dictionary = new HashMap<>();

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

    private void getStopwords() {
        try {
            FileInputStream fis = new FileInputStream(stopWordsFile);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                stopwords.add(line.trim());
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception ex ) {
            System.out.println(ex.getMessage());
        }
    }

    private void getDictionary() {
        try {
            FileInputStream fis = new FileInputStream(IDF);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                String[] part = line.split("\t");
                dictionary.put(part[0], count++);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private int[] getVector(String filePath) {
        int[] x = new int[dictionary.size()];
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
                        if (stopwords.contains(part)) {
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
                        if (!dictionary.containsKey(part)) {
                            continue;
                        }

                        x[dictionary.get(part)] += 1;
                    }
                }
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return x;
    }

    private double sqrt(int[] x) {
        int sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * x[i];
        }

        return Math.sqrt(sum);
    }

    private double calculateCosine(int[] x, int[] y, double sqrtX) {
        double sqrtY = sqrt(y);
        int numerator = 0;
        for (int i = 0; i < x.length; i++) {
            numerator += x[i] * y[i];
        }

        return (double) numerator / (sqrtX * sqrtY);
    }

    public List<String> getTopN(int N, String filePath) {
        getStopwords();
        getDictionary();
        int[] x = getVector(filePath);
        double sqrtX = sqrt(x);
        //System.out.println(sqrtX);

        PriorityQueue<Pair> pq = new PriorityQueue<>(new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                if (o2.cosine > o1.cosine) {
                    return -1;
                } else if (o2.cosine < o1.cosine) {
                    return 1;
                } else {
                    return o1.name.compareTo(o2.name);
                }
            }
        });
        List<String> ret = new ArrayList<>();

        for (int i = 1; i <= 4544; i++) {
            String file = rootPath + i + ".html";
            int[] y = getVector(file);
            double cosine = calculateCosine(x, y, sqrtX);

            if (pq.size() <= N) {
                pq.offer(new Pair(i + ".html", cosine));
            } else {
                if (cosine > pq.peek().cosine) {
                    pq.poll();
                    pq.offer(new Pair(i + ".html", cosine));
                }
            }
        }

        for (int i = 0; i < N; i++) {
            ret.add(pq.peek().name);
            System.out.println(pq.poll().cosine);
        }

        return ret;
    }
}
