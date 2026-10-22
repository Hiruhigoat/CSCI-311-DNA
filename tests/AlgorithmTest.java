package tests;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algorithms.EditDistance;
import algorithms.LCS;

/**
 * Test driver for LCS and EditDistance.
 * CSCI 311 DNA Project - Team 1
 *
 * Runs unit tests, then compares the query against every database sequence
 * and prints a table. Has its own small FASTA reader so it works without
 * the group's parser.
 *
 *   javac *.java
 *   java AlgorithmTest DNA_query.txt DNA_sequences.txt
 */
public class AlgorithmTest {

    /** One FASTA record: the header without its '>', and the sequence. */
    static class Record {
        String header;
        String sequence;

        Record(String header, String sequence) {
            this.header = header;
            this.sequence = sequence;
        }
    }

    /**
     * Read a FASTA file. A header starts with '>'. Lines after it join
     * together until the next header or end of file. A file with no header
     * is one unnamed sequence, which is how the query file is formatted.
     */
    static List<Record> readFasta(String path) throws IOException {
        List<Record> records = new ArrayList<>();
        StringBuilder seq = new StringBuilder();
        String header = null;

        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(">")) {
                    if (seq.length() > 0) {
                        records.add(new Record(header == null ? "(unnamed)" : header, seq.toString()));
                        seq.setLength(0);
                    }
                    header = line.substring(1).trim();
                } else if (!line.isEmpty()) {
                    seq.append(line);
                }
            }
        }

        if (seq.length() > 0) {
            records.add(new Record(header == null ? "(unnamed)" : header, seq.toString()));
        }
        return records;
    }

    /** Small cases we can check by hand. */
    static void sanityChecks() {
        System.out.println("Sanity checks");
        System.out.println("-------------");

        check("LCS empty",        LCS.lcsLength("", "ACGT"), 0);
        check("LCS identical",    LCS.lcsLength("ACGT", "ACGT"), 4);
        check("LCS disjoint",     LCS.lcsLength("AAAA", "CCCC"), 0);
        check("LCS spec example", LCS.lcsLength("CGACT", "CGGACAT"), 5);
        check("LCS textbook",     LCS.lcsLength("ABCBDAB", "BDCABA"), 4);

        check("ED empty",         EditDistance.distance("", "ACGT"), 4);
        check("ED identical",     EditDistance.distance("ACGT", "ACGT"), 0);
        check("ED one sub",       EditDistance.distance("ACGT", "ACTT"), 1);
        check("ED one insert",    EditDistance.distance("ACGT", "ACCGT"), 1);
        check("ED kitten",        EditDistance.distance("kitten", "sitting"), 3);

        // Argument order must not matter. Catches bugs in the row swapping.
        check("LCS symmetric",    LCS.lcsLength("ACGTACGT", "TTACG"),
                                  LCS.lcsLength("TTACG", "ACGTACGT"));
        check("ED symmetric",     EditDistance.distance("ACGTACGT", "TTACG"),
                                  EditDistance.distance("TTACG", "ACGTACGT"));

        // The traceback should give a real subsequence, not just a length.
        String got = LCS.lcsString("CGACT", "CGGACAT");
        System.out.printf("  %-18s %s (length %d)%n", "LCS traceback", got, got.length());
        System.out.println();
    }

    static void check(String name, int actual, int expected) {
        String mark = (actual == expected) ? "pass" : "FAIL";
        System.out.printf("  %-18s %s  got %d, expected %d%n", name, mark, actual, expected);
    }

    public static void main(String[] args) throws IOException {
        sanityChecks();

        if (args.length < 2) {
            System.out.println("Usage: java AlgorithmTest <queryFile> <databaseFile>");
            return;
        }

        List<Record> queryRecords = readFasta(args[0]);
        List<Record> database = readFasta(args[1]);
        if (queryRecords.isEmpty()) {
            System.out.println("No query sequence found in " + args[0]);
            return;
        }

        String query = queryRecords.get(0).sequence;
        System.out.printf("Query length: %d bp%n", query.length());
        System.out.printf("Database: %d sequences%n%n", database.size());

        System.out.printf("%-58s %6s %8s %8s %8s %8s%n",
                "sequence", "len", "LCS", "LCSnorm", "editDist", "edit%");
        System.out.println("-".repeat(102));

        String bestLcsName = null, bestNormName = null, bestEditName = null;
        int bestLcs = -1, bestEdit = Integer.MAX_VALUE;
        double bestNorm = -1.0;
        long start = System.currentTimeMillis();

        for (Record r : database) {
            int lcs = LCS.lcsLength(query, r.sequence);
            double norm = LCS.normalizedSim(query, r.sequence);
            int ed = EditDistance.distance(query, r.sequence);
            double pct = EditDistance.percentSimilarity(query, r.sequence);

            String label = r.header.length() > 56 ? r.header.substring(0, 56) : r.header;
            System.out.printf("%-58s %6d %8d %8.3f %8d %7.1f%%%n",
                    label, r.sequence.length(), lcs, norm, ed, pct * 100);

            if (lcs > bestLcs)   { bestLcs = lcs;   bestLcsName = r.header; }
            if (norm > bestNorm) { bestNorm = norm; bestNormName = r.header; }
            if (ed < bestEdit)   { bestEdit = ed;   bestEditName = r.header; }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println();
        System.out.println("Best match by raw LCS:        " + bestLcsName);
        System.out.println("Best match by normalized LCS: " + bestNormName);
        System.out.println("Best match by edit distance:  " + bestEditName);
        System.out.printf("%nSearched %d sequences in %d ms%n", database.size(), elapsed);
    }
}
