package mnkgame;

import java.util.Comparator;
import java.util.TreeSet;

public class AlphaBetaPruningPlayer implements MNKPlayer {
	private Heuristics heuristics;
	private MNKBoard board;
	private Comparator<MNKCellHeuristic> cresc;
	private Comparator<MNKCellHeuristic> decresc;

	private class MNKCellHeuristic extends MNKCell {
		double estimate;
		public MNKCellHeuristic(int i, int j, MNKCellState state, double estimate) {
			super(i, j, state);
			this.estimate = estimate;
		}
	}

	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		heuristics = new Heuristics(M, N, K, first, timeout_in_secs);
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
		long start = System.currentTimeMillis();

		// Recover the last move
		if (MC.length > 0) {
			MNKCell c = MC[MC.length - 1];
			board.markCell(c.i, c.j);
		}

		// Only one possible move
		if (FC.length == 1)
			return FC[0];

		// ALPHABETA
		MNKCell bestCell = null;
		double bestEval = Double.NEGATIVE_INFINITY;
		int depth = 0;
		TreeSet<MNKCellHeuristic> cells = getNextMoves(FC, board, true);
		for(MNKCellHeuristic cell: cells) {
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

		// situazione vantaggiosa/svantaggiosa totale
		if (Double.isInfinite(estimate))
			return myTurn ? heuristics.LARGE - depth : -heuristics.LARGE + depth;

//		 TODO: add condizione di tempo scaduto
		if (depth == 2 || FC.length ==  0 || board.gameState() != MNKGameState.OPEN)
			return estimate;

		TreeSet<MNKCellHeuristic> cells = getNextMoves(FC, board, myTurn);
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

	TreeSet<MNKCellHeuristic> getNextMoves(MNKCell FC[], MNKBoard board, boolean isDecresc) {
		TreeSet<MNKCellHeuristic> cells = new TreeSet(isDecresc ? decresc : cresc);
		double estimate;
		// O(n log n)
		for (MNKCell cell : FC) {
			board.markCell(cell.i, cell.j);
			estimate = heuristics.evaluate(board, cell);
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
