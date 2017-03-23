import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by Flynn on 19/03/2017.
 */
public class Test {
    static class Pair {
        String word;
        int count;
        public Pair(String word, int count) {
            this.count = count;
            this.word = word;
        }
    }

    private static final String tagRegex = "<([^>]*)>";
    // Replace this with the html website's path, e.g. "/Applications/IndriSEO/IndriSEO/data/220.html"
    private static String path = "";
    // Replace this with the "stop" file downloaded from Git repository, e.g. "/Users/Flynn/Desktop/eBusiness/Task 10/stop"
    private static final String stopWordsFile = "";

    public static void main(String[] args) {
        TermFrequency tf = new TermFrequency();
        Map<String, Integer> map = tf.getMap(path, stopWordsFile);
        List<Pair> list = new ArrayList<>();
        for (String s : map.keySet()) {
            list.add(new Pair(s, map.get(s)));
        }
        Collections.sort(list, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                if (o1.count != o2.count) {
                    return o2.count - o1.count;
                }
                return o1.word.compareTo(o2.word);
            }
        });

        for (Pair p : list) {
            System.out.println(p.word + "\t" + p.count);
        }
    }
}
