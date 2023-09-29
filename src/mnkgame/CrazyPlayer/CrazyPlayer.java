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
public MNKCell selectCell(MNKCell[] firstCells, MNKCell[] markedCells) {
	long startTime = System.currentTimeMillis();
	ai.setStart(startTime);

	if (markedCells.length > 0) {
		// Recover the last move
		MNKCell lastMove = markedCells[markedCells.length - 1];
		board.markCell(lastMove.i, lastMove.j);
	} else {
		// First to play
		MNKCell middleCell = new MNKCell(M / 2, N / 2);
		board.markCell(middleCell.i, middleCell.j);
		return middleCell;
	}

	// Only one possible move
	if (firstCells.length == 1)
		return firstCells[0];

	MNKCellEstimate bestCell = null;
	int depth = 1;
	try {
		MNKCell[] closedCells = ai.getClosedCells(markedCells, board.getBoardState());
		TreeSet<MNKCellEstimate> cells = ai.getBestMoves(closedCells, board, false);

		if (debug) ai.showSelectedCells(cells, MC);

		// Iterative Deepening
		// O(n^d) con n = cells.size() e d = depth
		for (; depth <= firstCells.length && depth <= AIHelper.DEPTH_LIMIT; depth++) {
			if (bestCell != null && bestCell.getEstimate() == AIHelper.LARGE || (ai.isTimeEnded()))
				break;

			MNKCellEstimate result = ai.alphabeta(board, cells, depth);
			bestCell = result;
		}
	} catch (TimeoutException e) {
		for (int i = board.getFreeCells().length; i < firstCells.length; i++)
			board.unmarkCell();
	}

	board.markCell(bestCell.i, bestCell.j);
	
	if (debug) ai.printPassedTimeAndMessage(getInfos(bestCell, depth));

	if (ai.canClearTT())
		ai.clearTT();

	return bestCell;
}

	private String getInfos(MNKCellEstimate bestCell, int depth) {
		return String.format("Number of calls: %d, Size of TT: %d \nBestCell: %s, Reached Depth: %d",
				AIHelper.numberOfCalls, ai.getTTSize(), bestCell, depth);
	}

	@Override
	public String playerName() {
		return "CrazyPlayer";
	}
}