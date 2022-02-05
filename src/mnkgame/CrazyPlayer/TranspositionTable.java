package mnkgame.CrazyPlayer;

import mnkgame.MNKBoard;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    enum BoundType {
        UPPERBOUND,
        LOWERBOUND,
        EXACT,
    }
    static class StoredValue {
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

    public StoredValue get(MNKBoard board) {
        currentBoardState = boardToString(board);
        // TODO: Verifica per ogni mirrored or turned boards
        return table.get(currentBoardState);
    }

    public String boardToString(MNKBoard board) {
        StringBuilder boardState = new StringBuilder();
        for (int i = 0; i < board.M; i++) {
            for (int j = 0; j < board.N; j++) {
                switch(board.cellState(i, j)) {
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

    private String formatBoardState(String boardState) {
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