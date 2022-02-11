package mnkgame.CrazyPlayer.model;


import mnkgame.CrazyPlayer.Util.BitBoard;

import java.util.BitSet;
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

    private Map<BitSet, StoredValue> table;
    private BitSet currentBoardState;
    private int M, N, K;

    public TranspositionTable(int M, int N, int K) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.table = new HashMap();
    }

    public void store(BitSet bitBoard, double alpha, double beta, double value, int depth) {
        // Replace by depth
        StoredValue entry = table.get(bitBoard);
        if (entry != null && entry.getDepth() > depth)
            return;

        StoredValue storedValue;
        if (value <= alpha)
            storedValue = new StoredValue(value, BoundType.UPPERBOUND, depth);
        else if (value >= beta)
            storedValue = new StoredValue(value, BoundType.LOWERBOUND, depth);
        else
            storedValue = new StoredValue(value, BoundType.EXACT, depth);

        table.put(bitBoard, storedValue);
    }

    public StoredValue get(MNKBoardEnhanced board) {
        // TODO: altre simmetrie, guarda rotazioni con bitboard
        // TODO: O()
        BitSet bitBoard = null;
        if (M == N)
            bitBoard = BitBoard.findRotatedBoard(table, board.getBoardState(), M);
        if (bitBoard == null)
            bitBoard = BitBoard.findMirrorBoard(table, board.getBoardState(), M, N);
        if (bitBoard == null)
            bitBoard = BitBoard.convertToBitBoard(board.getBoardState(), M, N);

        currentBoardState = bitBoard;
        return table.get(bitBoard);
    }

    public BitSet getCurrentBoardState() {
        return currentBoardState;
    }

    public int getSize() {
        return table.size();
    }

    public void clear() {
        table.clear();
    }

}