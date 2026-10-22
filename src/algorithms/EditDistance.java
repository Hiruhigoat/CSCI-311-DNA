package algorithms;
/**
 * Edit distance (Levenshtein) similarity.
 * CSCI 311 DNA Project - Team 1
 *
 * The fewest insertions, deletions, and substitutions needed to turn one
 * sequence into the other. Where LCS asks how much two sequences share,
 * this asks how much work separates them.
 */
public class EditDistance {

    /**
     * Edit distance between s and t.
     *
     * D[i][j] = distance between s[0..i-1] and t[0..j-1]:
     *   i                                           if j is 0
     *   j                                           if i is 0
     *   D[i-1][j-1]                                 if the last characters match
     *   1 + min(D[i-1][j], D[i][j-1], D[i-1][j-1])  otherwise
     *
     * Those three options are delete, insert, and substitute. Same two-row
     * trick as LCS. O(mn) time, O(min(m,n)) space.
     */
    public static int distance(String s, String t) {
        s = s.toUpperCase();
        t = t.toUpperCase();

        // Make t the shorter string so our rows are as short as possible.
        if (t.length() > s.length()) {
            String tmp = s;
            s = t;
            t = tmp;
        }

        int m = s.length();
        int n = t.length();
        if (n == 0) return m;

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        // Base case: turning an empty prefix of s into t[0..j-1] takes j inserts.
        for (int j = 0; j <= n; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= m; i++) {
            curr[0] = i;
            char sc = s.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                if (sc == t.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    int del = prev[j];
                    int ins = curr[j - 1];
                    int sub = prev[j - 1];
                    curr[j] = 1 + Math.min(sub, Math.min(del, ins));
                }
            }
            int[] swap = prev;
            prev = curr;
            curr = swap;
        }

        return prev[n];
    }

    /**
     * Similarity for the search loop. Higher is more similar.
     *
     * Edit distance is a distance, so smaller means more alike, but the
     * search loop keeps the largest score. So we negate it. Print
     * distance() to the user, not this.
     */
    public static double computeSim(String s, String t) {
        return -distance(s, t);
    }

    /**
     * Distance as a percent, for display only. The longer length is the
     * worst the distance can be, so dividing by it gives a ratio in [0,1].
     */
    public static double percentSimilarity(String s, String t) {
        int longer = Math.max(s.length(), t.length());
        if (longer == 0) return 1.0;
        return 1.0 - ((double) distance(s, t) / longer);
    }
}
