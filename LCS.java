/**
 * Longest Common Subsequence similarity.
 * CSCI 311 DNA Project - Team 1
 *
 * A subsequence does not have to be contiguous, so this counts how many
 * nucleotides two sequences share in the same order.
 */
public class LCS {

    /**
     * Length of the longest common subsequence of s and t.
     *
     * L[i][j] = LCS length of s[0..i-1] and t[0..j-1]:
     *   0                           if i or j is 0
     *   L[i-1][j-1] + 1             if the last characters match
     *   max(L[i-1][j], L[i][j-1])   otherwise
     *
     * Each row only needs the row above it, so we keep two rows instead of
     * the full table. O(mn) time, O(min(m,n)) space.
     */
    public static int lcsLength(String s, String t) {
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

        for (int i = 1; i <= m; i++) {
            char sc = s.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                if (sc == t.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            // Swap the rows and reuse the old one as scratch.
            int[] swap = prev;
            prev = curr;
            curr = swap;
        }

        return prev[n];
    }

    /**
     * The subsequence itself, not just its length. Needs the full table to
     * trace back through, so O(mn) space. Only used on the winning pair.
     */
    public static String lcsString(String s, String t) {
        s = s.toUpperCase();
        t = t.toUpperCase();
        int m = s.length();
        int n = t.length();

        int[][] L = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    L[i][j] = L[i - 1][j - 1] + 1;
                } else {
                    L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]);
                }
            }
        }

        // Walk back from L[m][n]. A diagonal step means those two characters
        // matched, so they belong in the answer.
        StringBuilder sb = new StringBuilder();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (s.charAt(i - 1) == t.charAt(j - 1)) {
                sb.append(s.charAt(i - 1));
                i--;
                j--;
            } else if (L[i - 1][j] >= L[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return sb.reverse().toString();
    }

    /**
     * Similarity for the search loop. Higher is more similar.
     * Note this is biased toward long sequences. See normalizedSim.
     */
    public static double computeSim(String s, String t) {
        return lcsLength(s, t);
    }

    /**
     * LCS length over the length of the longer sequence, so the score lands
     * in [0,1]. Dividing by the longer one is what removes the length bias:
     * a sequence now gets charged for every nucleotide it failed to match.
     */
    public static double normalizedSim(String s, String t) {
        if (s.isEmpty() || t.isEmpty()) return 0.0;
        return (double) lcsLength(s, t) / Math.max(s.length(), t.length());
    }
}
