package mnkgame.CrazyPlayer.Util;

import mnkgame.MNKCellState;

import java.util.BitSet;
import java.util.Map;

public class BitBoard {

	public static BitSet convertToBitBoard(MNKCellState[][] board, int M, int N) {
		// bitBoard = bitBoardP1 + bitBoardP2
		// e.g. 100100010 + 000010001
		BitSet bitBoard = new BitSet(M * N * 2);
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				switch(board[i][j]) {
					case P1:
						bitBoard.set(mapToMatrixIndex(i, j, N));
						break;
					case P2:
						bitBoard.set(mapToMatrixIndex(i, j, N) + (M * N));
						break;
				}
			}
		}

		return bitBoard;
	}

	public static BitSet findMirrorBoard(Map table, MNKCellState[][] board, int M, int N) {
		// TODO: missing diagonals mirror
		MNKCellState[][] horMirrBoard = new MNKCellState[M][N];
		MNKCellState[][] verMirrBoard = new MNKCellState[M][N];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				horMirrBoard[i][j] = board[M - i - 1][j];
				verMirrBoard[i][j] = board[i][N - j - 1];
			}
		}
		BitSet horBitBoard = convertToBitBoard(horMirrBoard, M, N);
		if (table.containsKey(horBitBoard))
			return horBitBoard;
		BitSet verBitBoard = convertToBitBoard(verMirrBoard, M, N);
		if (table.containsKey(verBitBoard))
			return verBitBoard;

		return null;
	}

	public static BitSet findRotatedBoard(Map table, MNKCellState[][] board, int M) {
		// clone board O(M^2)
		MNKCellState[][] rotBoard = new MNKCellState[M][M];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++)
				rotBoard[i][j] = board[i][j];
		}

		// right rotation => Trasposta + swap columns
		int rotationNumber = 0;
		while (rotationNumber < 3) {
			// Trova trasposta
			for (int i = 0; i < M; i++) {
				for (int j = i; j < M; j++) {
					MNKCellState temp = rotBoard[i][j];
					rotBoard[i][j] = rotBoard[j][i];
					rotBoard[j][i] = temp;
				}
			}
			// swap columns
			for (int i = 0; i < M; i++) {
				int low = 0, high = M - 1;
				while (low < high) {
					MNKCellState temp = rotBoard[i][low];
					rotBoard[i][low] = rotBoard[i][high];
					rotBoard[i][high] = temp;
					low++;
					high--;
				}
			}

			BitSet bitBoard = convertToBitBoard(rotBoard, M, M);
			if (table.containsKey(bitBoard))
				return bitBoard;
			rotationNumber++;
		}

		return null;
	}

	public static String formatBitBoard(BitSet bitBoard, int M, int N) {
		StringBuilder board = new StringBuilder();
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				if (bitBoard.get(mapToMatrixIndex(i, j, N)))
					board.append('X');
				else if (bitBoard.get(mapToMatrixIndex(i, j, N) + (M * N)))
					board.append('O');
				else
					board.append('-');
			}
			board.append('\n');
		}
		return board.toString();
	}

	public static String formatBoard(MNKCellState[][] board, int M, int N) {
		BitSet bitBoard = convertToBitBoard(board, M, N);
		return formatBitBoard(bitBoard, M, N);
	}

	public static int mapToMatrixIndex(int i, int j, int N) {
		return (i * N) + j;
	}
}
