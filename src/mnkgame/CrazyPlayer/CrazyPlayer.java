package mnkgame.CrazyPlayer;

import mnkgame.CrazyPlayer.Util.AIHelper;
import mnkgame.CrazyPlayer.model.MNKBoardEnhanced;
import mnkgame.CrazyPlayer.model.MNKCellEstimate;
import mnkgame.MNKCell;
import mnkgame.MNKPlayer;

import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

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
		board = new MNKBoardEnhanced(M, N, K);
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
			MNKCell middleCell = new MNKCell(M / 2, N / 2);
			board.markCell(middleCell.i, middleCell.j);
			return middleCell;
		}

		// Only one possible move
		if (FC.length == 1)
			return FC[0];


		MNKCellEstimate bestCell = null;
		int depth = 1;
		try {
			// O(n log n)
			MNKCell[] closedCells = ai.getClosedCells(MC, board.getBoardState());
			TreeSet<MNKCellEstimate> cells = ai.getBestMoves(closedCells, board, false);
			if (debug) 	ai.showSelectedCells(cells, MC);
			// O() Iterative Deepening
			for (; depth <= FC.length && depth <= 10; depth++) {
				if (bestCell != null && bestCell.getEstimate() == AIHelper.LARGE ||
				   (ai.isTimeEnded()))
					break;

				MNKCellEstimate res = ai.alphabeta(board, cells, depth);
				bestCell = res;
			}
		} catch (TimeoutException e) {
//			e.printStackTrace();
			for (int i = board.getFreeCells().length; i < FC.length; i++)
				board.unmarkCell();
		}

		board.markCell(bestCell.i, bestCell.j);
		if (debug) 	ai.printPassedTimeAndMessage(getInfos(bestCell, depth));

		// TODO: clear TT?
//		if (ai.canClearTT())
//			ai.clearTT();

		return bestCell;
	}

	private String getInfos(MNKCellEstimate bestCell, int depth) {
		return String.format("Number of calls: %d, Size of TT: %d \nBestCell: %s, Reached Depth: %d",
				AIHelper.numberOfCalls, ai.getTTSize(), bestCell, depth);
	}

	@Override
	public String playerName() {
		return "CrazyPlayerPazzissimo";
	}
}