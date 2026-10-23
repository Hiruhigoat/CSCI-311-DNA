package algorithms;

import userinterface.DNASequence;

public class NeedlemanWunsch {

    DNASequence s;
    DNASequence t;
    
    // Diagonal arrow = 0
    // Left arrow = -1
    // Top arrow = 1
    String[][] directionGrid;
    int[][] scoringGrid;
    

    int matchScore = 1;
    int misMatchScore = -1;
    int gapScore = -2;

    int score;
    String allignedSequenceS;
    String allignedSequenceT;

    public NeedlemanWunsch(DNASequence target, DNASequence sequence)
    {
        t = target;
        s = sequence;

        this.directionGrid =  new String[s.getSequenceLength() + 1][t.getSequenceLength() + 1];
        this.scoringGrid =  new int[s.getSequenceLength() + 1][t.getSequenceLength() + 1];

        makeGrid(s, t);
        score = getScore();
        getBestAlignment();
    }

    private void getBestAlignment()
    {
        int i = s.getSequenceLength();
        int j = t.getSequenceLength();
        StringBuilder stringSequenceS = new StringBuilder();
        StringBuilder stringSequenceT = new StringBuilder();

        while(i > 0 || j > 0)
        {
            switch (directionGrid[i][j]) {
                case "Diagonal Arrow":
                    stringSequenceS.append(s.getSequence().charAt(i - 1));
                    stringSequenceT.append(t.getSequence().charAt(j - 1));
                    i--;
                    j--;
                    break;
                case "Up Arrow":
                    stringSequenceS.append(s.getSequence().charAt(i - 1));
                    stringSequenceT.append('-');
                    i--;
                    break;
                case "Left Arrow":
                    stringSequenceS.append('-');
                    stringSequenceT.append(t.getSequence().charAt(j - 1));
                    j--;
                    break;
            }
        }
        this.allignedSequenceS = stringSequenceS.reverse().toString();
        this.allignedSequenceT = stringSequenceT.reverse().toString();
    }

    private int getScore()
    {
        return scoringGrid[s.getSequenceLength()][t.getSequenceLength()];
    }

    private void makeGrid(DNASequence s, DNASequence t)
    {
        for (int i = 0; i < s.getSequenceLength() + 1; i++)
        {
            scoringGrid[i][0] = gapScore * i;
            directionGrid[i][0] = "Up Arrow";
        }

        for (int j = 0; j < t.getSequenceLength() + 1; j++)
        {
            scoringGrid[0][j] = gapScore * j;
            directionGrid[0][j] = "Left Arrow";
        }

        for(int i = 1; i < s.getSequenceLength() + 1; i++)
        {
            for(int j = 1; j < t.getSequenceLength() + 1; j++)
            {
                int diagonal = Diagonal(i, j);
                int top = Top(i, j);
                int left = Left(i, j);

                int maxScore = Math.max(diagonal, Math.max(top, left));
                scoringGrid[i][j] = maxScore;

                if (maxScore == diagonal)
                    directionGrid[i][j] = "Diagonal Arrow";
                else if (maxScore == top)
                    directionGrid[i][j] = "Up Arrow";
                else 
                    directionGrid[i][j] = "Left Arrow";
            }
        }
    }

    private int Diagonal(int x, int y){
        int score = ((s.getSequence().charAt(x - 1)) == (t.getSequence().charAt(y - 1))) ?  scoringGrid[x - 1][y - 1] + matchScore : scoringGrid[x - 1][y - 1] + misMatchScore;
        return score;
    }
    
    private int Top(int x, int y){
        return scoringGrid[x - 1][y] + gapScore;
    }
    
    private int Left(int x, int y){
        return scoringGrid[x][y - 1] + gapScore;
    }
}