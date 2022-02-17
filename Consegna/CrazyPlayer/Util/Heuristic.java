package mnkgame.CrazyPlayer.Util;

import mnkgame.CrazyPlayer.model.MNKBoardEnhanced;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

import java.util.HashSet;
import java.util.Set;

public class Heuristic {
	private final int M, N, K;
	private final MNKGameState myWin, yourWin;
	private final int myPlayer;

	public Heuristic(int M, int N, int K, boolean first) {
		this.M = M;
		this.N = N;
		this.K = K;
		myPlayer = first ? 0 : 1;
		myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
	}

	public double evaluate(MNKBoardEnhanced board, MNKCell lastCell) {
		MNKGameState state = board.gameState();
		double eval;
		if (state == MNKGameState.DRAW)
			return 0;
		else if (state == myWin || state == yourWin)
			eval = Double.POSITIVE_INFINITY;
		else
			eval = findThreats(board, lastCell, board.getBoardState());

		return board.currentPlayer() != myPlayer ? eval : (-eval + 0.0);
	}

	// O(8K)
	private double findThreats(MNKBoardEnhanced board, MNKCell lastCell, MNKCellState[][] boardState) {
		int newThreat = 0; // evaluate su threats creati da lastCell
		int blockThreat = 0;  // evaluate su threats avversari bloccati da lastCell

		Set<Integer> checkCells = new HashSet<>();
		checkCells.add(BitBoard.mapToMatrixIndex(lastCell.i, lastCell.j, N));

		MNKCellState myState = board.lastPlayer() == 0 ? MNKCellState.P1 : MNKCellState.P2;
		for (int i = lastCell.i - 1; i <= lastCell.i + 1; i++) {
			// Out of bound
			if (i < 0 || i >= M)
				continue;
			for (int j = lastCell.j - 1; j <= lastCell.j + 1; j++) {
				// Out of bound
				if (j < 0 || j >= N)
					continue;

				// opposite direction threat already checked || FREE cell
				if (checkCells.contains(BitBoard.mapToMatrixIndex(i, j, N)) || boardState[i][j] == MNKCellState.FREE)
					continue;

				boolean[] openOuter = {false, false};

				// check in opposite direction too
				int prev_i = getNextDirectionIndex(i, lastCell.i);
				int prev_j = getNextDirectionIndex(j, lastCell.j);

				// O(K)
				int lenT = 1 + findLenThreat(boardState, boardState[i][j], i, j, lastCell.i, lastCell.j, openOuter);

				if (prev_i >= 0 && prev_i < M && prev_j >= 0 && prev_j < N) {
					if (boardState[prev_i][prev_j] == boardState[i][j]) {
						openOuter[0] = openOuter[1];
						openOuter[1] = false;
						lenT += findLenThreat(boardState, boardState[prev_i][prev_j], prev_i, prev_j, lastCell.i, lastCell.j, openOuter);
						checkCells.add(BitBoard.mapToMatrixIndex(prev_i, prev_j, N));
					} else if (boardState[prev_i][prev_j] == MNKCellState.FREE)
						openOuter[0] = true;
				}

				if (boardState[i][j] == myState) {
					if (lenT == K - 1 && (openOuter[0] && openOuter[1]))         // fai doppia mossa
						newThreat += 50;
					else if (lenT == K - 1 && (openOuter[0] || openOuter[1]))    // gioca se non è close-type
						newThreat += 25;
					else if (lenT == K - 2 && (openOuter[0] && openOuter[1]))	 // open type
						newThreat += 10;
					else if (lenT == K - 2 && (openOuter[0] || openOuter[1]))	// half-open
						newThreat += 5;
					else														// sommo la lunghezza del threat trovato
						newThreat += lenT;
				} else {
					if (lenT == K)          									// blocca vittoria
						return AIHelper.LARGE / 10;
					else if (lenT == K - 1 && (openOuter[0] && openOuter[1]))   // blocca doppia mossa
						blockThreat += (myState == MNKCellState.P1) ? 25 : 50;
					else if (lenT == K - 1 && (openOuter[0] || openOuter[1]))   // blocca se non è close-type
						blockThreat += (myState == MNKCellState.P1) ? 5 : 10;
				}
			}
		}

		return newThreat + blockThreat;
	}

	// O(K)
	private int findLenThreat(MNKCellState[][] board, MNKCellState state, int i, int j, int prev_i, int prev_j, boolean[] openOuter) {
		if (i < 0 || i >= M || j < 0 || j >= N)
			return 0;
		if (board[i][j] != state) {
			if (board[i][j] == MNKCellState.FREE)
				openOuter[1] = true;
			return 0;
		} else
			return 1 + findLenThreat(board, board[i][j], getNextDirectionIndex(prev_i, i), getNextDirectionIndex(prev_j, j), i, j, openOuter);
	}

	private int getNextDirectionIndex(int prev_coord, int coord) {
		return coord + (coord - prev_coord);
	}

}
