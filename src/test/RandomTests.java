package test;

import mnkgame.CrazyPlayer.Util.AIHelper;
import mnkgame.CrazyPlayer.Util.BitBoard;
import mnkgame.CrazyPlayer.model.TranspositionTable;
import mnkgame.CrazyPlayer.model.MNKBoardEnhanced;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import org.junit.Test;

import java.util.BitSet;
import java.util.Random;
import java.util.Scanner;

import static mnkgame.MNKGameState.OPEN;

public class RandomTests {

	@Test
	public void boardToStringTest() {
		TranspositionTable table = new TranspositionTable(3, 3, 3);
		MNKBoardEnhanced board = new MNKBoardEnhanced(3, 3, 3);
		board.markCell(0, 0);
		board.markCell(1, 1);
		board.markCell(2, 2);

		BitSet bitBoard = BitBoard.convertToBitBoard(board.getBoardState(), 3, 3);
		System.out.println(BitBoard.formatBitBoard(bitBoard, 3, 3));
	}

	@Test
	public void mathMinInfinity() {
		double inf = Double.POSITIVE_INFINITY;
		double menoInf = Double.NEGATIVE_INFINITY;
		System.out.println(Double.isInfinite(inf));
		System.out.println(Double.isInfinite(menoInf));
	}

	@Test
	public void rotatedBoards() {
		TranspositionTable table = new TranspositionTable(4, 4, 4);
		MNKBoardEnhanced board = new MNKBoardEnhanced(4, 4, 4);
		board.markCell(0, 2);
		board.markCell(1, 1);
		board.markCell(3, 0);
		board.markCell(3, 1);
		board.markCell(0, 1);

		BitSet bitBoard = BitBoard.convertToBitBoard(board.getBoardState(), 3, 3);
		System.out.println(BitBoard.formatBitBoard(bitBoard, 3, 3));

//		System.out.println("Rotation:");
//		BitBoard.findRotatedBoard(board.getBoardState());
//		System.out.println("Mirror:");
//		BitBoard.findMirrorBoard(board.getBoardState());
	}

	@Test
	public void bitBoardTest() {
		TranspositionTable table = new TranspositionTable(3, 3, 3);
		MNKBoardEnhanced board = new MNKBoardEnhanced(3, 3, 3);
		board.markCell(0, 2);
		board.markCell(1, 0);
		board.markCell(1, 1);
		board.markCell(0, 1);

		BitSet bitBoard = BitBoard.convertToBitBoard(board.getBoardState(), 3, 3);
		System.out.println(bitBoard.toString());
		System.out.println(BitBoard.formatBitBoard(bitBoard, 3, 3));
	}

	@Test
	public void findThreatsTest() {
		int M = 4;
		int N = 4;
		int K = 4;
		MNKBoardEnhanced board = new MNKBoardEnhanced(M, N, K);
		AIHelper ai = new AIHelper(M, N, K, true, 10);
		Random random = new Random(System.currentTimeMillis());
		/*
		- - - - 	- - - -
		- X O -	 => - X O -
		- O X - 	- O X -
		- - - - 	X - - -
					lc(3, 3)
		 */

		while (board.gameState() == OPEN) {
			MNKCell c;
			do {
				c = new MNKCell(random.nextInt(M), new Random().nextInt(N));
			} while (board.getBoardState()[c.i][c.j] != MNKCellState.FREE);
			board.markCell(c.i, c.j);
//			printEvaluate(board, c, M, N, ai);
		}

		System.out.println(board.gameState());

	}

//	private void printEvaluate(MNKBoardEnhanced board, MNKCell lastCell, int M, int N, AIHelper ai) {
//		MNKCell[] MC = board.getMarkedCells();
//		System.out.print(BitBoard.formatBoard(board.getBoardState(), M, N));
//		System.out.println(ai.evaluate(board, lastCell) + "\n");
//	}

}
