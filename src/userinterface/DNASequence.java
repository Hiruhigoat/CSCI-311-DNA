package userinterface;

// DNA Sequence object that has a header of the sequence and the sequence itself
public class DNASequence {
    private final String header;
    private final String sequence;

    public DNASequence(String header, String sequence) {
        this.header = header;
        this.sequence = sequence;
    }

    public String getHeader() {
        return header;
    }

    public String getSequence() {
        return sequence;
    }

    public int getSequenceLength()
    {
        return sequence.length();
    }
 
    @Override
    public String toString() {
        return "Header: " + header + "\nSequence Length: " + sequence.length();
    }
}
