package mnkgame.CrazyPlayer.Util;

import mnkgame.CrazyPlayer.model.MNKBoardEnhanced;
import mnkgame.CrazyPlayer.model.MNKCellEstimate;
import mnkgame.CrazyPlayer.model.TranspositionTable;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

public class AIHelper {
    public static int numberOfCalls;
    public static final int DEPTH_LIMIT = 10;
    public static final double LARGE = 1e3;

    private final TranspositionTable transTable;
    private final Heuristic heuristic;
    private final int M, N, K;
    private final int timeout;
    private long start;

    public AIHelper(int M, int N, int K, boolean first, int timeout) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.timeout = timeout;
        this.heuristic = new Heuristic(M, N, K, first);
        this.transTable = new TranspositionTable(M, N, K);
    }

    // O(n * √(m^d))
    public MNKCellEstimate alphabeta(MNKBoardEnhanced board, TreeSet<MNKCellEstimate> cells, int depth) throws TimeoutException {
        MNKCellEstimate bestCell = null;
        double bestEval = Double.NEGATIVE_INFINITY;
        AIHelper.numberOfCalls = 0;
        // O(n)
        for (MNKCellEstimate cell : cells) {
            if (isTimeEnded() || bestEval == AIHelper.LARGE) break;

            board.markCell(cell.i, cell.j);
            double eval = alphabeta(board, cell.getEstimate(), true, -AIHelper.LARGE, AIHelper.LARGE, depth);
            if (eval > bestEval) {
                bestEval = eval;
                bestCell = cell;
                bestCell.setEstimate(bestEval);
            }
            board.unmarkCell();
        }
        return bestCell;
    }

    // O(√(m^d))
    private double alphabeta(MNKBoardEnhanced board, double estimate, boolean myNode, double a, double b, int depth) throws TimeoutException {
        // for debug
        numberOfCalls = numberOfCalls + 1;

        // situazione vantaggiosa totale
        if (Double.isInfinite(estimate))
            return myNode ? AIHelper.LARGE : -AIHelper.LARGE;
        if (depth == 0 || board.gameState() != MNKGameState.OPEN || isTimeEnded())
            return estimate;

        double aOrig = a;
        // TranspositionTable Lookup
        TranspositionTable.StoredValue entry = transTable.get(board);
        BitSet boardState = transTable.getCurrentBoardState();
        if (entry != null && entry.getDepth() >= depth) {
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

        MNKCell[] FC = getClosedCells(board.getMarkedCells(), board.getBoardState());
        TreeSet<MNKCellEstimate> cells = getBestMoves(FC, board, myNode);
        double eval;
        if (myNode) {
            eval = Double.POSITIVE_INFINITY;
            for (MNKCellEstimate cell : cells) {
                board.markCell(cell.i, cell.j);
                eval = Math.min(eval, alphabeta(board, cell.getEstimate(), false, a, b, depth-1));
                board.unmarkCell();
                b = Math.min(eval, b);
                if (b <= a)             // a cutoff
                    break;
            }
        } else {
            eval = Double.NEGATIVE_INFINITY;
            for (MNKCellEstimate cell : cells) {
                board.markCell(cell.i, cell.j);
                eval = Math.max(eval, alphabeta(board, cell.getEstimate(), true, a, b, depth-1));
                board.unmarkCell();
                a = Math.max(eval, a);
                if (b <= a)             // b cutoff
                    break;
            }
        }

        // TranspositionTable Store
        transTable.store(boardState, aOrig, b, eval, depth);

        return eval;
    }

    // O(n log n)
    public TreeSet<MNKCellEstimate> getBestMoves(MNKCell[] FC, MNKBoardEnhanced board, boolean asc) throws TimeoutException {
        TreeSet<MNKCellEstimate> cells = new TreeSet(MNKCellEstimate.getCellComparator(asc));
        // O(n)
        for (MNKCell cell : FC) {
            isTimeEnded();

            board.markCell(cell.i, cell.j);
            double estimate = heuristic.evaluate(board, cell);
            board.unmarkCell();
            // O(log n)
            cells.add(new MNKCellEstimate(cell.i, cell.j, estimate));
            // non ha senso procedere con il calcolo dell'estimate per le altre celle se mi accorgo di essere
            // in una situazione vantaggiosa totale
            if (Double.isInfinite(estimate))
                break;
        }
        return cells;
    }

    public MNKCell[] getClosedCells(MNKCell[] MC, MNKCellState[][] boardState) {
//        int bound = M * N <= 16 ? 2 : 1;
        int bound = 1;
        Set<MNKCell> FC = new HashSet<>();
        for (MNKCell mc : MC) {
            for (int i = mc.i - bound; i <= mc.i + bound; i++) {
                if (i < 0 || i >= M)    // Out of bound
                    continue;
                for (int j = mc.j - bound; j <= mc.j + bound; j++) {
                    if (j < 0 || j >= N ||     // Out of bound
                       (i == mc.i && j == mc.j))
                        continue;
                    if (boardState[i][j] == MNKCellState.FREE)
                        FC.add(new MNKCell(i, j));
                }
            }
        }
        MNKCell[] arrFC = new MNKCell[FC.size()];
        return FC.toArray(arrFC);
    }

    public void setStart(long start) {
        this.start = start;
    }

    public boolean isTimeEnded() throws TimeoutException {
        if ((System.currentTimeMillis()-start) / 1000.0 > timeout*(98.0/100.0))
            throw new TimeoutException("TIME ENDED");
        return false;
    }

    int selectCellCalls = 0;
    public boolean canClearTT() {
        if (++selectCellCalls == 4) {
            selectCellCalls = 0;
            return true;
        }
        return false;

    }

    public void clearTT() {
        transTable.clear();
    }

    public int getTTSize() {
        return transTable.getSize();
    }

    // DEBUG

    public void showSelectedCells(TreeSet<MNKCellEstimate> cells, MNKCell[] MC) {
        boolean showLetters = false; // change it if you want
        showSelectedCells(cells, MC, false);
    }

    public void showSelectedCells(TreeSet<MNKCellEstimate> cells, MNKCell[] MC, boolean showLetters) {
        System.out.printf("%d: %s\n", cells.size(), cells);
        char[][] board = new char[M][N];
        for (int row = 0; row < M; row++) {
            for (int col = 0; col < N; col++)
                board[row][col] = '.';
        }
        int i = 97;
        for (MNKCellEstimate cell : cells)
            board[cell.i][cell.j] = showLetters ? (char) i++ : '-';
        for (MNKCell cell : MC)
            board[cell.i][cell.j] = cell.state == MNKCellState.P1 ? 'X':'O';
        for (char[] row : board) {
            for (char col : row)
                System.out.printf(" %c ", col);
            System.out.println();
        }
    }

    public void printPassedTimeAndMessage(String message) {
        System.out.printf("%s, Time: %d\n", message, System.currentTimeMillis()-start);
    }

}
