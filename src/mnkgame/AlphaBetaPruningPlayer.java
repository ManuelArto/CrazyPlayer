package mnkgame;

import java.util.ArrayList;
import java.util.List;

public class AlphaBetaPruningPlayer implements MNKPlayer {
	private MNKBoard board;
	private MNKGameState myWin, yourWin;
	
	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		board = new MNKBoard(M, N, K);
		myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
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

		MNKCell bestCell = null;
		float bestEval = Float.NEGATIVE_INFINITY;
		int depth = 0;
		for(MNKCell cell: FC) {
			board.markCell(cell.i, cell.j);
			float moveEval = alphabeta(board, true, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, depth);
			if (moveEval >= bestEval) {
				bestEval = moveEval;
				bestCell = cell;
			}
			board.unmarkCell();
		}
		board.markCell(bestCell.i, bestCell.j);

		System.out.println((System.currentTimeMillis() - start) / 1000.0);

		return bestCell;
	}

	private float alphabeta (MNKBoard board, boolean myTurn, float a, float b, int depth) {
		MNKCell FC[] = board.getFreeCells();
		
		if (FC.length ==  0 || board.gameState() != MNKGameState.OPEN)
			return evaluate(board, depth);

		if (myTurn) {
			for (MNKCell cell : FC) {
				board.markCell(cell.i, cell.j);
				float eval = alphabeta(board, false, a, b, depth++);
				board.unmarkCell();
				b = Math.min(eval, b);
				if (b <= a)
					return a;
			}
			return b;
		} else {
			for (MNKCell cell : FC) {
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