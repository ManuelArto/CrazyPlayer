package mnkgame;

import java.util.Comparator;
import java.util.TreeSet;

public class AlphaBetaPruningPlayer implements MNKPlayer {
	private AIHelper AIHelper;
	private int M, N, K;
	private MNKBoard board;
	private Comparator<MNKCellHeuristic> cresc;
	private Comparator<MNKCellHeuristic> decresc;
	private long start;

	private class MNKCellHeuristic extends MNKCell {
		double estimate;
		public MNKCellHeuristic(int i, int j, MNKCellState state, double estimate) {
			super(i, j, state);
			this.estimate = estimate;
		}
	}

	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		this.M = M;
		this.K = K;
		this.N = N;
		AIHelper = new AIHelper(M, N, K, first, timeout_in_secs);
		board = new MNKBoard(M, N, K);
		decresc =  (b1, b2) -> {
			if (b1.estimate - b2.estimate == 0.0) return 1;	// TreeSet non contiene "duplicati"
			return (int) (b1.estimate - b2.estimate);
		};
		cresc =  (b1, b2) -> {
			if (b2.estimate - b1.estimate == 0.0) return -1;
			return (int) (b2.estimate - b1.estimate);
		};
	}

	@Override
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		start = System.currentTimeMillis();

		if (MC.length > 0) {
			// Recover the last move
			MNKCell c = MC[MC.length - 1];
			board.markCell(c.i, c.j);
		} else {
			// First to play
			MNKCell middleCell = new MNKCell(M/2, N/2);
			board.markCell(middleCell.i, middleCell.j);
			return middleCell;
		}

		// Only one possible move
		if (FC.length == 1)
			return FC[0];

		// ALPHABETA
		MNKCell bestCell = null;
		double bestEval = Double.NEGATIVE_INFINITY;
		int depth = 0;
		TreeSet<MNKCellHeuristic> cells = getBestMoves(FC, board, true);
		for(MNKCellHeuristic cell: cells) {
			if (AIHelper.isTimeEnded(start)) break;

			board.markCell(cell.i, cell.j);
			double moveEval = alphabeta(board, cell.estimate, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth);
			if (moveEval >= bestEval) {
				bestEval = moveEval;
				bestCell = cell;
			}
			board.unmarkCell();
		}
		board.markCell(bestCell.i, bestCell.j);

		System.out.println(System.currentTimeMillis() - start);

		return bestCell;
	}

	private double alphabeta(MNKBoard board, double estimate, boolean myTurn, double a, double b, int depth) {
		MNKCell FC[] = board.getFreeCells();

		// situazione vantaggiosa totale
		if (Double.isInfinite(estimate))
			return myTurn ? AIHelper.LARGE - depth : -AIHelper.LARGE + depth;

		if (FC.length ==  0 || board.gameState() != MNKGameState.OPEN || AIHelper.isTimeEnded(start))
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

	TreeSet<MNKCellHeuristic> getBestMoves(MNKCell FC[], MNKBoard board, boolean myTurn) {
		TreeSet<MNKCellHeuristic> cells = new TreeSet(myTurn ? decresc : cresc);
		double estimate;
		// O(n log n)
		for (MNKCell cell : FC) {
			if (AIHelper.isTimeEnded(start)) break;

			board.markCell(cell.i, cell.j);
			estimate = AIHelper.evaluate(board, cell);
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

	@Override
	public String playerName() {
		return "AlphaBetaPruningPlayer";
	}
}
