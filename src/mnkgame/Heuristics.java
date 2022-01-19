package mnkgame;

public class Heuristics {

    static final double LARGE = 100.0;

    private int M, N, K;
    private boolean first;
    private int timeout_in_secs;
    private MNKGameState myWin, yourWin;


    public Heuristics(int M, int N, int K, boolean first, int timeout_in_secs) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.first = first;
        this.timeout_in_secs = timeout_in_secs;
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
    }

    public double evaluate(MNKBoard board, MNKCell lastCell) {
        MNKGameState state = board.gameState();
        if (state == myWin || state == yourWin)
            return Double.POSITIVE_INFINITY;
        return 0;
        // qui euristiche su partita ancora aperta

    }


}
