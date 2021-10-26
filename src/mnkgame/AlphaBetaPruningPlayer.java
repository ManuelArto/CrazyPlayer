package mnkgame;

import java.util.ArrayList;
import java.util.List;

public class AlphaBetaPruningPlayer implements MNKPlayer {
	private MNKBoard board;
	private MNKGameState myWin, yourWin;
	public class Evaluation {
		float eval;
		MNKCell cell;
		Evaluation(){}
		Evaluation(Float eval) {this.eval = eval;}
	}
	
	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		board = new MNKBoard(M, N, K);
		myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
	}
	
	@Override
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {

		// Recover the last move
		if (MC.length > 0) {
			MNKCell c = MC[MC.length - 1];
			board.markCell(c.i, c.j);
		}
		
		// Only one possible move
		if (FC.length == 1)
			return FC[0];

		Evaluation eval = alphabeta(board, true, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		
		return eval.cell;
	}

	private Evaluation alphabeta (MNKBoard board, boolean myTurn, float a, float b) {
		Evaluation eval;
		MNKCell FC[] = board.getFreeCells();
		
		if (FC.length ==  1)
			return evaluate(board, FC[0]);
		else if (myTurn) {
			eval = new Evaluation(Float.POSITIVE_INFINITY);
			for (MNKCell cell : FC) {
				board.markCell(eval.cell.i, eval.cell.j);
				Evaluation compareEval = alphabeta(board, false, a, b);
				board.unmarkCell();
				if (compareEval.eval < eval.eval)
					eval = compareEval;
				b = Math.min(eval.eval, b);
				if (b <= a)
					break;
			}
		} else {
			eval = new Evaluation(Float.NEGATIVE_INFINITY);
			for (MNKCell cell : FC) {
				board.markCell(eval.cell.i, eval.cell.j);
				Evaluation compareEval = alphabeta(board, true, a, b);
				board.unmarkCell();
				if (compareEval.eval > eval.eval)
					eval = compareEval;
				a = Math.max(eval.eval, a);
				if (b <= a)
					break;
			}
		}

		return eval;
	}

	private Evaluation evaluate(MNKBoard board, MNKCell cell) {
		Evaluation e = new Evaluation();
		MNKGameState state = board.markCell(cell.i, cell.i);
		if (state == myWin)
			e.eval = 1;
		else if (state == yourWin)
			e.eval = -1;
		else
			e.eval = 0;
		e.cell = cell;
		return e;
	}

	@Override
	public String playerName() {
		return "AlphaBetaPruningPlayer";
	}
}
