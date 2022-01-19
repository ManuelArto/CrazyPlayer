package mnkgame;

public class AIHelper {

    static final double LARGE = 100.0;

    final int M, N, K;
    final boolean first;
    final int timeout;
    MNKGameState myWin, yourWin;


    public AIHelper(int M, int N, int K, boolean first, int timeout) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.first = first;
        this.timeout = timeout;
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
    }

    public double evaluate(MNKBoard board, MNKCell lastCell) {
        MNKGameState state = board.gameState();
        if (state == myWin)
            return Double.POSITIVE_INFINITY;
        else if (state == yourWin)
            return Double.NEGATIVE_INFINITY;
        return 0;
        // qui euristiche su partita ancora aperta

    }

    // find threats

    public boolean isTimeEnded(long start) {
        boolean isTimeEnded = (System.currentTimeMillis()-start) / 1000.0 > timeout*(99.0/100.0);
        return isTimeEnded;
    }

}
