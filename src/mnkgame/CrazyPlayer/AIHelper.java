package mnkgame.CrazyPlayer;

import mnkgame.MNKBoard;
import mnkgame.MNKCell;
import mnkgame.MNKGameState;

import java.util.Comparator;
import java.util.TreeSet;

public class AIHelper {
    static final double LARGE = 1e4;

    private final int M, N, K;
    private final boolean first;
    private final int timeout;
    private MNKGameState myWin, yourWin;
    private int myPlayer;
    private long start;
    private Comparator<MNKCellEstimate> cresc;
    private Comparator<MNKCellEstimate> decresc;
    private int[] rowBounds;
    private int[] colBounds;

    public static class MNKCellEstimate extends MNKCell {
        double estimate;
        public MNKCellEstimate(int i, int j, double estimate) {
            super(i, j);
            this.estimate = estimate;
        }
        @Override
        public String toString() {
            return super.toString() + " - " + this.estimate;
        }
    }

    public static class MNKBoardEstimate extends MNKBoard {
        public MNKBoardEstimate(int M, int N, int K) throws IllegalArgumentException {
            super(M, N, K);
        }
        public void switchPlayer() { currentPlayer = (currentPlayer + 1) % 2; }
    }

    public AIHelper(int M, int N, int K, boolean first, int timeout) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.first = first;
        this.timeout = timeout;
        myPlayer = first ? 0 : 1;
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

    public TreeSet<MNKCellEstimate> getBestMoves(MNKCell FC[], MNKBoardEstimate board, boolean myTurn) {
        TreeSet<MNKCellEstimate> cells = new TreeSet(myTurn ? decresc : cresc);
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
            cells.add(new MNKCellEstimate(cell.i, cell.j, estimate));
            // non ha senso procedere con il calcolo dell'estimate per le altre celle se mi accorgo di essere
            // in una situazione vantaggiosa/svantaggiosa totale
            if (Double.isInfinite(estimate))
                break;
        }
//        this.printPassedTimeAndMessage(cells.toString());
        return cells;
    }

    public double alphabeta(MNKBoardEstimate board, double estimate, boolean myTurn, double a, double b, int depth) {
        MNKCell FC[] = board.getFreeCells();

        // situazione vantaggiosa/svantaggiosa totale
        if (Double.isInfinite(estimate))
            return myTurn ? AIHelper.LARGE - depth : -AIHelper.LARGE + depth;

        if (depth == 3 || FC.length ==  0 || board.gameState() != MNKGameState.OPEN || isTimeEnded())
            return estimate;

        TreeSet<MNKCellEstimate> cells = getBestMoves(FC, board, myTurn);
        double eval;
        if (myTurn) {
            eval = Double.POSITIVE_INFINITY;
            for (MNKCellEstimate cell : cells) {
                board.markCell(cell.i, cell.j);
                eval = Math.min(eval, alphabeta(board, cell.estimate, false, a, b, depth++));
                board.unmarkCell();
                b = Math.min(eval, b);
                if (b <= a)             // a cutoff
                    break;
            }
        } else {
            eval = Double.NEGATIVE_INFINITY;
            for (MNKCellEstimate cell : cells) {
                board.markCell(cell.i, cell.j);
                eval = Math.max(eval, alphabeta(board, cell.estimate, true, a, b, depth++));
                board.unmarkCell();
                a = Math.max(eval, a);
                if (b <= a)             // b cutoff
                    break;
            }
        }
        return eval;
    }

    public double evaluate(MNKBoardEstimate board, MNKCell lastCell) {
        MNKGameState state = board.gameState();
        if (state == MNKGameState.DRAW)
            return 0;
        else if (state == myWin || state == yourWin || blockAWin(board, lastCell))
            return board.currentPlayer() == myPlayer ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

        // qui euristiche su partita ancora aperta

        return 0;

    }

    private boolean blockAWin(MNKBoardEstimate board, MNKCell lastCell) {
        for (int i = 0; i < 2; i++) {
            board.unmarkCell();
            board.switchPlayer();
            MNKGameState state = board.markCell(lastCell.i, lastCell.j);
            if (state == yourWin || state == myWin)
                return true;
        }
        return false;
    }

    // find threats


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
