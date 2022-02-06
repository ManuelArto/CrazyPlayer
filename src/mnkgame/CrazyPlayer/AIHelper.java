package mnkgame.CrazyPlayer;

import mnkgame.CrazyPlayer.model.MNKBoardEnhanced;
import mnkgame.CrazyPlayer.model.MNKCellEstimate;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

import java.util.TreeSet;

public class AIHelper {
    public static int numberOfCalls;
    static final double LARGE = 1e3;

    private final int M, N, K;
    private final int timeout;
    private final MNKGameState myWin, yourWin;
    private final int myPlayer;
    private final TranspositionTable transTable;
    private long start;

    public AIHelper(int M, int N, int K, boolean first, int timeout) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.timeout = timeout;
        myPlayer = first ? 0 : 1;
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        this.transTable = new TranspositionTable(M, N, K);
    }

    public TreeSet<MNKCellEstimate> getBestMoves(MNKCell[] FC, MNKBoardEnhanced board, boolean myTurn) {
        TreeSet<MNKCellEstimate> cells = new TreeSet(MNKCellEstimate.getCellComparator(myTurn));
        MNKCell[] MC = board.getMarkedCells();
        // O(n log n)
        for (MNKCell cell : FC) {
            if (isTimeEnded())
                break;
            if (!isCellInBounds(MC, cell))
                continue;

            board.markCell(cell.i, cell.j);
            // TODO: verifica se conviene TT lookup here
            double estimate = evaluate(board, cell);
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

    public double alphabeta(MNKBoardEnhanced board, double estimate, boolean myTurn, double a, double b, int depth) {
        // for debug
        numberOfCalls = numberOfCalls + 1;

        double aOrig = a;
        // TranspositionTable Lookup
        TranspositionTable.StoredValue entry = transTable.get(board);
        String boardState = transTable.getCurrentBoardState();
        if (entry != null && entry.getDepth() <= depth) {
            switch (entry.getFlag()) {
                case EXACT:
                    return entry.getValue();
                case UPPERBOUND:
                    b = Math.min(b, entry.getValue());
                    break;
                case LOWERBOUND:
                    a = Math.max(a, entry.getValue());
                    break;
            }
            if (a >= b)
                return entry.getValue();
        }

        MNKCell[] FC = board.getFreeCells();

        // situazione vantaggiosa/svantaggiosa totale
        if (Double.isInfinite(estimate))
            return myTurn ? AIHelper.LARGE - depth : -AIHelper.LARGE + depth;
        if (depth == 5 || FC.length ==  0 || board.gameState() != MNKGameState.OPEN || isTimeEnded())
            return estimate;

        TreeSet<MNKCellEstimate> cells = getBestMoves(FC, board, myTurn);
        double eval;
        if (myTurn) {
            eval = Double.POSITIVE_INFINITY;
            for (MNKCellEstimate cell : cells) {
                board.markCell(cell.i, cell.j);
                eval = Math.min(eval, alphabeta(board, cell.getEstimate(), false, a, b, depth+1));
                board.unmarkCell();
                b = Math.min(eval, b);
                if (b <= a)             // a cutoff
                    break;
            }
        } else {
            eval = Double.NEGATIVE_INFINITY;
            for (MNKCellEstimate cell : cells) {
                board.markCell(cell.i, cell.j);
                eval = Math.max(eval, alphabeta(board, cell.getEstimate(), true, a, b, depth+1));
                board.unmarkCell();
                a = Math.max(eval, a);
                if (b <= a)             // b cutoff
                    break;
            }
        }

        // TranspositionTable Store
        transTable.store(boardState, aOrig, b, eval, depth, myTurn);

        return eval;
    }

    // TODO: remove later
    public void showSelectedCells(TreeSet<MNKCellEstimate> cells, MNKCell[] MC) {
        char[][] board = new char[M][N];
        for (int row = 0; row < M; row++) {
            for (int col = 0; col < N; col++)
                board[row][col] = '.';
        }
        for (MNKCellEstimate cell : cells)
            board[cell.i][cell.j] = '-';
        for (MNKCell cell : MC)
            board[cell.i][cell.j] = cell.state == MNKCellState.P1 ? 'X':'O';
        for (char[] row : board) {
            for (char col : row)
                System.out.printf(" %c ", col);
            System.out.println();
        }
    }

    public double evaluate(MNKBoardEnhanced board, MNKCell lastCell) {
        MNKGameState state = board.gameState();
        if (state == MNKGameState.DRAW)
            return 0;
        else if (state == myWin || state == yourWin)
            return board.currentPlayer() != myPlayer ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        else if (blockAWin(board, lastCell))
            return (board.currentPlayer() != myPlayer ? 1 : -1) * LARGE / 10;

        // qui euristiche su partita ancora aperta

        return 0;

    }

    private boolean blockAWin(MNKBoardEnhanced board, MNKCell lastCell) {
        board.unmarkCell();
        board.switchPlayer();
        MNKGameState state = board.markCell(lastCell.i, lastCell.j);
        board.unmarkCell();
        board.switchPlayer();
        board.markCell(lastCell.i, lastCell.j);
        return (state == yourWin || state == myWin);
    }

    // find threats

    public boolean isCellInBounds(MNKCell[] MC, MNKCell cell) {
        // TODO: check bound
        int bound = M * N <= 1000 ? 1 : 2;
        for (MNKCell mc : MC) {
            boolean rowBound = (cell.i >= mc.i - bound && cell.i <= mc.i + bound);
            boolean colBound = (cell.j >= mc.j - bound && cell.j <= mc.j + bound);
            if (rowBound && colBound)
                return true;
        }

        return false;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public boolean isTimeEnded() {
        return (System.currentTimeMillis()-start) / 1000.0 > timeout*(99.0/100.0);
    }

    public void printPassedTimeAndMessage(String message) {
        System.out.printf("Time: %d: %s%n\n", System.currentTimeMillis()-start, message);
    }

    public int getTTSize() {
        return transTable.getSize();
    }
}
