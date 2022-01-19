package mnkgame;

import java.util.Comparator;
import java.util.TreeSet;

public class AIHelper {
    static final double LARGE = 100.0;

    final int M, N, K;
    final boolean first;
    final int timeout;
    MNKGameState myWin, yourWin;
    private long start;
    private Comparator<MNKCellHeuristic> cresc;
    private Comparator<MNKCellHeuristic> decresc;

    public class MNKCellHeuristic extends MNKCell {
        double estimate;
        public MNKCellHeuristic(int i, int j, MNKCellState state, double estimate) {
            super(i, j, state);
            this.estimate = estimate;
        }
    }

    public AIHelper(int M, int N, int K, boolean first, int timeout) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.first = first;
        this.timeout = timeout;
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        decresc =  (b1, b2) -> {
            if (b1.estimate - b2.estimate == 0.0) return 1;	// TreeSet non contiene "duplicati"
            return (int) (b1.estimate - b2.estimate);
        };
        cresc =  (b1, b2) -> {
            if (b2.estimate - b1.estimate == 0.0) return -1;
            return (int) (b2.estimate - b1.estimate);
        };
    }

    public TreeSet<MNKCellHeuristic> getBestMoves(MNKCell FC[], MNKBoard board, boolean myTurn) {
        TreeSet<MNKCellHeuristic> cells = new TreeSet(myTurn ? decresc : cresc);
        double estimate;
        // O(n log n)
        for (MNKCell cell : FC) {
            if (isTimeEnded())
                break;
            board.markCell(cell.i, cell.j);
            estimate = evaluate(board, cell);
            board.unmarkCell();
            // O(log n)
            cells.add(new MNKCellHeuristic(cell.i, cell.j, cell.state, estimate));
            // non ha senso procedere con il calcolo dell'estimate per le altre celle se mi accorgo di essere
            // in una situazione vantaggiosa/svantaggiosa totale
            if (Double.isInfinite(estimate))
                break;
        }
        return cells;
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


    public double alphabeta(MNKBoard board, double estimate, boolean myTurn, double a, double b, int depth) {
        MNKCell FC[] = board.getFreeCells();

        // situazione vantaggiosa totale
        if (Double.isInfinite(estimate))
            return myTurn ? AIHelper.LARGE - depth : -AIHelper.LARGE + depth;

        if (FC.length ==  0 || board.gameState() != MNKGameState.OPEN || isTimeEnded())
            return estimate;

        TreeSet<MNKCellHeuristic> cells = getBestMoves(FC, board, myTurn);
        if (myTurn) {
            for (MNKCellHeuristic cell : cells) {
                board.markCell(cell.i, cell.j);
                double eval = alphabeta(board, cell.estimate, false, a, b, depth++);
                board.unmarkCell();
                b = Math.min(eval, b);
                if (b <= a)
                    return a;
            }
            return b;
        } else {
            for (MNKCellHeuristic cell : cells) {
                board.markCell(cell.i, cell.j);
                double eval = alphabeta(board, cell.estimate, true, a, b, depth++);
                board.unmarkCell();
                a = Math.max(eval, a);
                if (b <= a)
                    return b;
            }
            return a;
        }
    }

    public void setStart(long start) {
        this.start = start;
    }

    public boolean isTimeEnded() {
        boolean isTimeEnded = (System.currentTimeMillis()-start) / 1000.0 > timeout*(99.0/100.0);
        return isTimeEnded;
    }

}