package mnkgame.CrazyPlayer.model;

import mnkgame.MNKCellState;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    public enum BoundType {
        UPPERBOUND,
        LOWERBOUND,
        EXACT,
    }
    public static class StoredValue {
        private double value;
        private BoundType flag;
        private int depth;
        StoredValue(double value, BoundType flag, int depth) {
            this.value = value;
            this.flag = flag;
            this.depth = depth;
        }
        public double getValue() { return value; }
        public BoundType getFlag() { return flag; }
        public int getDepth() { return depth; }
    }

    private Map<String, StoredValue> table;
    private String currentBoardState;
    private int M, N, K;

    public TranspositionTable(int M, int N, int K) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.table = new HashMap();
    }

    public void store(String boardState, double alpha, double beta, double value, int depth, boolean myTurn) {
        StoredValue storedValue;
        if (value <= alpha)
            storedValue = new StoredValue(value, BoundType.UPPERBOUND, depth);
        else if (value >= beta)
            storedValue = new StoredValue(value, BoundType.LOWERBOUND, depth);
        else
            storedValue = new StoredValue(value, BoundType.EXACT, depth);

        table.put(boardState, storedValue);
//        System.out.printf("BoardState: \n%s", formatBoardState(boardState));
//        System.out.printf("Value: %f, Type: %s, Depth: %d, MyTurn: %b\n\n",
//                storedValue.getValue(), storedValue.getFlag(), storedValue.getDepth(), myTurn);
    }

    public StoredValue get(MNKBoardEnhanced board) {
        String boardState = null;
        // TODO verificare se conviene calcolare ogni volta turned and mirrored board oppure clonare i valori
        // TODO: ci sono problema più simmetrie
        // Ottimizzazione vs spazio
        if (M == N)
            boardState = findRotatedBoard(board.getBoardState());
        if (boardState == null)
            boardState = findMirrorBoard(board.getBoardState());
        if (boardState == null)
            boardState = boardToString(board.getBoardState());

        currentBoardState = boardState;
        return table.get(boardState);
    }

    public String boardToString(MNKCellState[][] board) {
        StringBuilder boardState = new StringBuilder();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                switch(board[i][j]) {
                    case FREE:
                        boardState.append("-");
                        break;
                    case P1:
                        boardState.append("X");
                        break;
                    case P2:
                        boardState.append("O");
                        break;
                }
            }
        }
        return boardState.toString();
    }

    public String getCurrentBoardState() {
        return currentBoardState;
    }

    public int getSize() {
        return table.size();
    }

    public String findMirrorBoard(MNKCellState[][] board) {
        MNKCellState[][] horMirrBoard = new MNKCellState[M][N];
        MNKCellState[][] verMirrBoard = new MNKCellState[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                horMirrBoard[i][j] = board[M - i - 1][j];
                verMirrBoard[i][j] = board[i][N - j - 1];
            }
        }
        String horBoardState = boardToString(horMirrBoard);
        if (table.containsKey(horBoardState))
            return horBoardState;
        String verBoardState = boardToString(verMirrBoard);
        if (table.containsKey(verBoardState))
            return verBoardState;

        return null;
    }

    public String findRotatedBoard(MNKCellState[][] board) {
        // clone board
        MNKCellState[][] rotBoard = new MNKCellState[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++)
                rotBoard[i][j] = board[i][j];
        }

        // right rotation => Trasposta + swap columns
        int rotationNumber = 0;
        while (rotationNumber < 3) {
            // Trova trasposta
            for (int i = 0; i < M; i++) {
                for (int j = i; j < N; j++) {
                    MNKCellState temp = rotBoard[i][j];
                    rotBoard[i][j] = rotBoard[j][i];
                    rotBoard[j][i] = temp;
                }
            }
            // swap columns
            for (int i = 0; i < M; i++) {
                int low = 0, high = N - 1;
                while (low < high) {
                    MNKCellState temp = rotBoard[i][low];
                    rotBoard[i][low] = rotBoard[i][high];
                    rotBoard[i][high] = temp;
                    low++;
                    high--;
                }
            }

            String boardState = boardToString(rotBoard);
            if (table.containsKey(boardState))
                return boardState;
            rotationNumber++;
        }

        return null;
    }

    public String formatBoardState(String boardState) {
        StringBuilder board = new StringBuilder();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                board.append(boardState.charAt(i * N + j));
            }
            board.append('\n');
        }
        return board.toString();
    }

}
/**
 *
 X  X  -				 -  -  X			  -  O  -			   -  -  -
 -  X  - ==> turn +90    O  X  X  && turn +90 -  X  -  && turn +90 X  X  O
 -  O  -				 -  -  -			  -  X  X			   X  -  -

 X  X  -			   -  X  X             -  O  -        -  -  -           X  -  -
 -  X  - ==> mirror LR -  X  -   mirror UP -  X  -    DL  O  X  X    DR     X  X  O
 -  O  -			   -  O  -             X  X  -        -  -  X           -  -  -
 */