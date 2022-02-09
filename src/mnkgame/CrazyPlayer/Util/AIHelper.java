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

public class AIHelper {
    public static int numberOfCalls;
    public static final double LARGE = 1e3;

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

    public TreeSet<MNKCellEstimate> getBestMoves(MNKCell[] FC, MNKBoardEnhanced board, boolean asc) {
        TreeSet<MNKCellEstimate> cells = new TreeSet(MNKCellEstimate.getCellComparator(asc));
        MNKCell[] MC = board.getMarkedCells();
        // O(n log n)
        for (MNKCell cell : FC) {
            if (!isCellInBounds(MC, cell))
                continue;

            board.markCell(cell.i, cell.j);
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

    public double alphabeta(MNKBoardEnhanced board, double estimate, boolean myNode, double a, double b, int depth) {
        // for debug
        numberOfCalls = numberOfCalls + 1;

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

        MNKCell[] FC = board.getFreeCells();

        // situazione vantaggiosa/svantaggiosa totale
        if (Double.isInfinite(estimate))
            return myNode ? AIHelper.LARGE : -AIHelper.LARGE;
        if (depth == 0 || FC.length ==  0 || board.gameState() != MNKGameState.OPEN || isTimeEnded())
            return estimate;

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

    public void showSelectedCells(TreeSet<MNKCellEstimate> cells, MNKCell[] MC) {
        boolean showLetters = false; // change it if you want
        showSelectedCells(cells, MC, false);
    }

    public void showSelectedCells(TreeSet<MNKCellEstimate> cells, MNKCell[] MC, boolean showLetters) {
        System.out.println(cells);
        char[][] board = new char[M][N];
        for (int row = 0; row < M; row++) {
            for (int col = 0; col < N; col++)
                board[row][col] = '.';
        }
        int i = 97;
        for (MNKCellEstimate cell : cells)
            board[cell.i][cell.j] = showLetters ? '-' : (char) i++; ;
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
        double eval;
        if (state == MNKGameState.DRAW)
            eval = 0;
        else if (state == myWin || state == yourWin)
            eval = Double.POSITIVE_INFINITY;
        else if (blockAWin(board, lastCell))
            eval = LARGE / 10;
        else
            eval = findThreats(board, lastCell, board.getBoardState().clone());

        return board.currentPlayer() != myPlayer ? eval : (-eval + 0.0);
    }

    private boolean blockAWin(MNKBoardEnhanced board, MNKCell lastCell) {
        if (board.getMarkedCells().length < (K-1)*2)
            return false;
        board.unmarkCell();
        board.switchPlayer();
        MNKGameState state = board.markCell(lastCell.i, lastCell.j);
        board.unmarkCell();
        board.switchPlayer();
        board.markCell(lastCell.i, lastCell.j);
        return (state == yourWin || state == myWin);
    }

    public double findThreats(MNKBoardEnhanced board, MNKCell lastCell, MNKCellState[][] boardState) {
        int newThreat = 0; // k-1 e k-2 threats creati da lastCell
        int blockThreat = 0;  // k-2 threat nemici bloccati da lastCell (con jump)

        Set<Integer> checkCells = new HashSet<>();
        checkCells.add(BitBoard.mapToMatrixIndex(lastCell.i, lastCell.j, N));

        MNKCellState myState = board.lastPlayer() == 0 ? MNKCellState.P1 : MNKCellState.P2;
        MNKCellState enemyState = board.currentPlayer() == 0 ? MNKCellState.P1 : MNKCellState.P2;
        for (int i = lastCell.i - 1; i <= lastCell.i + 1; i++) {
            if (i < 0 || i >= M)
                continue;
            for (int j = lastCell.j - 1; j <= lastCell.j + 1; j++) {
                if (j < 0 || j >= N)
                    continue;
                if (checkCells.contains(BitBoard.mapToMatrixIndex(i, j, N)) || boardState[i][j] == MNKCellState.FREE)
                    continue;

                int lenT = findLenThreat(boardState, boardState[i][j], i, j, lastCell.i, lastCell.j);
                // check in opposite direction if possible
                int prev_i = getNextDirectionIndex(i, lastCell.i);
                int prev_j = getNextDirectionIndex(j, lastCell.j);
                if (prev_i >= 0 && prev_i < M &&
                    prev_j >= 0 && prev_j < N &&
                    boardState[prev_i][prev_j] == boardState[i][j]) {
                    lenT += findLenThreat(boardState, boardState[prev_i][prev_j], prev_i, prev_j, lastCell.i, lastCell.j);
                    checkCells.add(BitBoard.mapToMatrixIndex(prev_i, prev_j, N));
                }

                if (boardState[i][j] == myState && lenT+1 == K - 1)
                    newThreat += 10;
                else if (boardState[i][j] == myState && lenT+1 == K - 2)
                    newThreat += 1;
                else if(boardState[i][j] == enemyState && lenT == K - 2)
                    blockThreat += 5;
            }
        }

        return newThreat + blockThreat;
    }

    private int findLenThreat(MNKCellState[][] board, MNKCellState state, int i, int j, int prev_i, int prev_j) {
        if (i < 0 || i >= M || j < 0 || j >= N || board[i][j] != state)
            return 0;
        else
            return 1 + findLenThreat(board, state, getNextDirectionIndex(prev_i, i), getNextDirectionIndex(prev_j, j), i, j);
    }

    private int getNextDirectionIndex(int prev_coord, int coord) {
        return coord + (coord - prev_coord);
    }

    public boolean isCellInBounds(MNKCell[] MC, MNKCell cell) {
        // TODO: check bound 1 o 2
        int bound = M * N <= 16 ? 1 : 1;
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
        return false;
//        return (System.currentTimeMillis()-start) / 1000.0 > timeout*(99.0/100.0);
    }

    public void printPassedTimeAndMessage(String message) {
        System.out.printf("%s, Time: %d\n", message, System.currentTimeMillis()-start);
    }

    public void clearTT() {
        transTable.clear();
    }

    public int getTTSize() {
        return transTable.getSize();
    }
}
