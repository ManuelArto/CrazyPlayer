package mnkgame.CrazyPlayer;

import mnkgame.MNKBoard;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

import java.util.Comparator;
import java.util.TreeSet;

public class AIHelper {
    static final double LARGE = 1e4;

    private final int M, N, K;
    private final boolean first;
    private final int timeout;
    private MNKGameState myWin, yourWin;
    private MNKCellState myState;
    private long start;
    private Comparator<MNKCellHeuristic> cresc;
    private Comparator<MNKCellHeuristic> decresc;
    private int[] rowBounds;
    private int[] colBounds;

    public static class MNKCellHeuristic extends MNKCell {
        double estimate;
        public MNKCellHeuristic(int i, int j, MNKCellState state, double estimate) {
            super(i, j, state);
            this.estimate = estimate;
        }
        @Override
        public String toString() {
            return super.toString() + " - " + this.estimate;
        }
    }

    public AIHelper(int M, int N, int K, boolean first, int timeout) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.first = first;
        this.timeout = timeout;
        myState = first ? MNKCellState.P1 : MNKCellState.P2;
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
            if (!isCellInBounds(cell))
                continue;

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
//        this.printPassedTimeAndMessage(cells.toString());
        return cells;
    }

    public double evaluate(MNKBoard board, MNKCell lastCell) {
        MNKGameState state = board.gameState();
        if (state == myWin)
            return Double.POSITIVE_INFINITY;
        else if (state == yourWin)
            return Double.NEGATIVE_INFINITY;
        else if (state == MNKGameState.DRAW)
            return 0;
        // qui euristiche su partita ancora aperta

        // Verifica che lastCell blocchi una vittoria dell'avversario =>

        return 0;

    }

    // find threats


    public double alphabeta(MNKBoard board, double estimate, boolean myTurn, double a, double b, int depth) {
        MNKCell FC[] = board.getFreeCells();

        // TODO: verifica se corretta gestione della profondità
        // situazione vantaggiosa/svantaggiosa totale
        if (Double.isInfinite(estimate))
            return myTurn ? AIHelper.LARGE + depth : -AIHelper.LARGE - depth;

        if (depth == 3 || FC.length ==  0 || board.gameState() != MNKGameState.OPEN || isTimeEnded())
            return estimate;

        TreeSet<MNKCellHeuristic> cells = getBestMoves(FC, board, myTurn);
        double eval;
        if (myTurn) {
            eval = Double.POSITIVE_INFINITY;
            for (MNKCellHeuristic cell : cells) {
                board.markCell(cell.i, cell.j);
                eval = Math.min(eval, alphabeta(board, cell.estimate, false, a, b, depth++));
                board.unmarkCell();
                b = Math.min(eval, b);
                if (b <= a)
                    break;
            }
        } else {
            eval = Double.NEGATIVE_INFINITY;
            for (MNKCellHeuristic cell : cells) {
                board.markCell(cell.i, cell.j);
                eval = Math.max(eval, alphabeta(board, cell.estimate, true, a, b, depth++));
                board.unmarkCell();
                a = Math.max(eval, a);
                if (b <= a)
                    break;
            }
        }
        return eval;
    }

    public boolean isCellInBounds(MNKCell cell) {
        boolean checkRow = cell.i >= rowBounds[0] && cell.i <= rowBounds[1];
        boolean checkCol = cell.j >= colBounds[0] && cell.j <= colBounds[1];
        return checkRow && checkCol;
    }

    public void updateBounds(MNKCell cell) {
        if (rowBounds == null || colBounds == null) {
            rowBounds = new int[] {cell.i - 1, cell.i + 1};
            colBounds = new int[] {cell.j - 1, cell.j + 1};
        } else {
            rowBounds[0] = Math.min(cell.i - 1, rowBounds[0]);
            rowBounds[1] = Math.max(cell.i + 1, rowBounds[1]);
            colBounds[0] = Math.min(cell.j - 1, colBounds[0]);
            colBounds[1] = Math.max(cell.j + 1, colBounds[1]);
        }
    }

    public void printBounds() {
        System.out.println(String.format("Row: %d/%d, Col: %d/%d", rowBounds[0], rowBounds[1], colBounds[0], colBounds[1]));
    }

    public void setStart(long start) {
        this.start = start;
    }

    public boolean isTimeEnded() {
        boolean isTimeEnded = (System.currentTimeMillis()-start) / 1000.0 > timeout*(99.0/100.0);
        return isTimeEnded;
    }

    public void printPassedTimeAndMessage(String message) {
        System.out.println(String.format("%d: %s", System.currentTimeMillis()-start, message));
    }

}
