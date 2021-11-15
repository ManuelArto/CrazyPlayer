package mnkgame;

import java.util.Comparator;
import java.util.TreeSet;

public class AlphaBetaPruningPlayer implements MNKPlayer {
	private Heuristics heuristics;
	private MNKBoard board;
	private MNKGameState myWin, yourWin;
	private Comparator<MNKCellHeuristic> cresc;
	private Comparator<MNKCellHeuristic> decresc;

	private class MNKCellHeuristic extends MNKCell {
		private float estimate;
		public MNKCellHeuristic(int i, int j, MNKCellState state, float estimate) { super(i, j, state); this.estimate = estimate; }
		public float getEstimate() { return estimate; }
		public void setEstimate(float estimate) { this.estimate = estimate; }
	}

	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		heuristics = new Heuristics();
		board = new MNKBoard(M, N, K);
		myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
		decresc =  (b1, b2) -> {
			if (b1.getEstimate() - b2.getEstimate() == 0.0) return 1;
			return (int) (b1.getEstimate() - b2.getEstimate());
		};
		cresc =  (b1, b2) -> {
			if (b2.getEstimate() - b1.getEstimate() == 0.0) return -1;
			return (int) (b2.getEstimate() - b1.getEstimate());
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
		float bestEval = Float.NEGATIVE_INFINITY;
		int depth = 0;
		TreeSet<MNKCellHeuristic> cells = getNextMoves(FC, board, true);
		for(MNKCell cell: cells) {
			board.markCell(cell.i, cell.j);
			float moveEval = alphabeta(board, true, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, depth);
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

	private float alphabeta(MNKBoard board, boolean myTurn, float a, float b, int depth) {
		MNKCell FC[] = board.getFreeCells();

		// float estimate = (Valutazione tramite euristiche sulla board e celle libere)

		/*
		if (estimate +- inf)
			situazione vantaggiosa/svantaggiosa totale
			return LARGE - depth;
		 */

		if (depth == 2 || FC.length ==  0 || board.gameState() != MNKGameState.OPEN)
			return evaluate(board, depth);	// return estimate

		TreeSet<MNKCellHeuristic> cells = getNextMoves(FC, board, myTurn);
		if (myTurn) {
			// O(n log n)
			for (MNKCellHeuristic cell : cells) {
				board.markCell(cell.i, cell.j);
				float eval = alphabeta(board, false, a, b, depth++);
				board.unmarkCell();
				b = Math.min(eval, b);
				if (b <= a)
					return a;
			}
			return b;
		} else {
			for (MNKCellHeuristic cell : cells) {
				board.markCell(cell.i, cell.j);
				float eval = alphabeta(board, true, a, b, depth++);
				board.unmarkCell();
				a = Math.max(eval, a);
				if (b <= a)
					return b;
			}
			return a;
		}
	}

	TreeSet<MNKCellHeuristic> getNextMoves(MNKCell FC[], MNKBoard board, boolean isDecresc) {
		TreeSet cells = new TreeSet(isDecresc ? cresc : decresc);
		float estimate;
		// O(n log n)
		for (MNKCell cell : FC) {
			board.markCell(cell.i, cell.j);
			// estimate = heuristics.estimate(board)
			estimate = 0;
			board.unmarkCell();
			// O(log n)
			cells.add(new MNKCellHeuristic(cell.i, cell.j, cell.state, estimate));
		}
		return cells;
	}


	private float evaluate(MNKBoard board, int depth) {
		MNKGameState state = board.gameState();
		if (state == myWin)
			return 10 - depth;
		else if (state == yourWin)
			return -10 + depth;
		return 0;
	}

	@Override
	public String playerName() {
		return "AlphaBetaPruningPlayer";
	}
}
