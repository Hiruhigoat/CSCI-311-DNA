package algorithms;
/**
 * Longest Common Substring similarity.
 * CSCI 311 DNA Project - Team 1
 *
 * A substring has to be contiguous in both sequences. Unlike LCS,
 * we cannot skip characters between matches.
 */
public class LongestCommonSubstring {

    /**
     * Length of the longest common substring of s and t.
     *
     * L[i][j] = length of the matching suffix ending at s[i-1] and t[j-1]:
     *   0                    if i or j is 0
     *   L[i-1][j-1] + 1      if the last characters match
     *   0                    otherwise
     *
     * The answer is the largest value anywhere in the table, not just
     * the bottom-right cell. We only need the previous row, so this
     * takes O(mn) time and O(min(m,n)) space.
     */
    public static int substringLength(String s, String t) {
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
        if (n == 0) return 0;

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];
        int best = 0;

        for (int i = 1; i <= m; i++) {
            char sc = s.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                if (sc == t.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                    best = Math.max(best, curr[j]);
                } else {
                    curr[j] = 0;
                }
            }
            int[] swap = prev;
            prev = curr;
            curr = swap;
        }

        return best;
    }

    /**
     * The substring itself, not just its length.
     * This also uses only two rows and returns uppercase DNA letters.
     * If several substrings have the same maximum length, returns one.
     */
    public static String substringString(String s, String t) {
        s = s.toUpperCase();
        t = t.toUpperCase();

        if (t.length() > s.length()) {
            String tmp = s;
            s = t;
            t = tmp;
        }

        int m = s.length();
        int n = t.length();
        if (n == 0) return "";

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];
        int best = 0;
        int end = 0;

        for (int i = 1; i <= m; i++) {
            char sc = s.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                if (sc == t.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                    if (curr[j] > best) {
                        best = curr[j];
                        end = i;
                    }
                } else {
                    curr[j] = 0;
                }
            }
            int[] swap = prev;
            prev = curr;
            curr = swap;
        }

        return s.substring(end - best, end);
    }

    /**
     * Similarity for the search loop. Higher is more similar.
     * As with raw LCS, long sequences may have an advantage.
     */
    public static double computeSim(String s, String t) {
        return substringLength(s, t);
    }

    /**
     * Length of the common substring divided by the longer sequence length.
     * Returns a value between 0 and 1 for display or comparison.
     */
    public static double normalizedSim(String s, String t) {
        if (s.isEmpty() || t.isEmpty()) return 0.0;
        return (double) substringLength(s, t) / Math.max(s.length(), t.length());
    }
}
