package mnkgame.CrazyPlayer.model;


import mnkgame.MNKBoard;
import mnkgame.MNKCellState;

public class MNKBoardEnhanced extends MNKBoard {

	public MNKBoardEnhanced(int M, int N, int K) throws IllegalArgumentException {
		super(M, N, K);
	}

	public void switchPlayer() {
		currentPlayer = (currentPlayer + 1) % 2;
	}

	public int lastPlayer() {
		return (currentPlayer + 1) % 2;
	}

	public MNKCellState[][] getBoardState() {
		return B;
	}
}