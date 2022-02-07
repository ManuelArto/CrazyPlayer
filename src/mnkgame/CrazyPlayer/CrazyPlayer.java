package mnkgame.CrazyPlayer;

import mnkgame.CrazyPlayer.model.MNKBoardEnhanced;
import mnkgame.CrazyPlayer.model.MNKCellEstimate;
import mnkgame.MNKCell;
import mnkgame.MNKPlayer;

import java.util.TreeSet;

public class CrazyPlayer implements MNKPlayer {
	private AIHelper ai;
	private int M, N, K;
	private MNKBoardEnhanced board;
	private boolean debug = false;

	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		this.M = M;
		this.N = N;
		this.K = K;
		ai = new AIHelper(M, N, K, first, timeout_in_secs);
		// TODO: check bound
		board = new MNKBoardEnhanced(M, N, K);
	}

	@Override
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		// TODO: valutare aggiunta di try-catch

		long start = System.currentTimeMillis();
		ai.setStart(start);

		if (MC.length > 0) {
			// Recover the last move
			MNKCell c = MC[MC.length - 1];
			board.markCell(c.i, c.j);
		} else {
			// First to play
			MNKCell middleCell = new MNKCell(M / 2, N / 2);
			board.markCell(middleCell.i, middleCell.j);
			return middleCell;
		}

		// Only one possible move
		if (FC.length == 1)
			return FC[0];

		MNKCell bestCell = null;
		double bestEval = Double.NEGATIVE_INFINITY;

		// ALPHABETA
		AIHelper.numberOfCalls = 0;
		TreeSet<MNKCellEstimate> cells = ai.getBestMoves(FC, board, true);
		if (debug)
			ai.showSelectedCells(cells, MC);
		for (MNKCellEstimate cell : cells) {
			if (ai.isTimeEnded() || bestEval == AIHelper.LARGE) break;

			board.markCell(cell.i, cell.j);
			double eval = ai.alphabeta(board, cell.getEstimate(), true, -AIHelper.LARGE, AIHelper.LARGE, 10);
			if (eval >= bestEval) {
				bestEval = eval;
				bestCell = cell;
			}
			board.unmarkCell();
		}
		board.markCell(bestCell.i, bestCell.j);

		if (debug)
			ai.printPassedTimeAndMessage(String.format("Number of calls: %d, Size of TT: %d", AIHelper.numberOfCalls, ai.getTTSize()));

		return bestCell;
	}

	@Override
	public String playerName() {
		return "CrazyPlayerPazzissimo";
	}
}