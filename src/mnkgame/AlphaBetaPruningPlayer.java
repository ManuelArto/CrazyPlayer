package mnkgame;


import java.util.TreeSet;

public class AlphaBetaPruningPlayer implements MNKPlayer {
	private AIHelper ai;
	private int M, N, K;
	private MNKBoard board;

	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		this.M = M;
		this.N = N;
		this.K = K;
		ai = new AIHelper(M, N, K, first, timeout_in_secs);
		board = new MNKBoard(M, N, K);
	}

	@Override
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		long start = System.currentTimeMillis();
		ai.setStart(start);

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
		TreeSet<AIHelper.MNKCellHeuristic> cells = ai.getBestMoves(FC, board, true);
		for(AIHelper.MNKCellHeuristic cell: cells) {
			if (ai.isTimeEnded()) break;

			board.markCell(cell.i, cell.j);
			double moveEval = ai.alphabeta(board, cell.estimate, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth);
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

	@Override
	public String playerName() {
		return "AlphaBetaPruningPlayer";
	}
}
