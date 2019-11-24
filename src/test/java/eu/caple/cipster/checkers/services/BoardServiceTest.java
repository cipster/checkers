package eu.caple.cipster.checkers.services;

import eu.caple.cipster.checkers.domain.model.Board;
import eu.caple.cipster.checkers.domain.model.Disk;
import eu.caple.cipster.checkers.domain.model.Move;
import eu.caple.cipster.checkers.domain.model.Position;
import eu.caple.cipster.checkers.domain.model.Square;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static eu.caple.cipster.checkers.domain.model.Disk.Color.BLACK;
import static eu.caple.cipster.checkers.domain.model.Disk.Color.RED;
import static eu.caple.cipster.checkers.domain.model.Move.Type.JUMP;
import static eu.caple.cipster.checkers.domain.model.Move.Type.SLIDE;
import static eu.caple.cipster.checkers.domain.model.Square.Type.DARK;
import static eu.caple.cipster.checkers.domain.model.Square.Type.LIGHT;
import static eu.caple.cipster.checkers.services.GameService.COLUMNS;
import static eu.caple.cipster.checkers.services.GameService.ROWS;
import static eu.caple.cipster.checkers.services.GameService.TOTAL_PIECES_PER_PLAYER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardServiceTest {

	private BoardService classUnderTest;

	@BeforeEach
	void setUp() {
		classUnderTest = new BoardService();
	}

	@Test
	void getStartingBoardHasExpectedNumberOfSquaresAndPieces() {
		Board result = classUnderTest.getStartingBoard();
		Set<Square> squares = result.getSquares();
		int totalNumberOfSquares = ROWS * COLUMNS;
		assertEquals(totalNumberOfSquares, squares.size());

		assertEquals(totalNumberOfSquares / 2, squares.stream()
				.filter(square -> square.getType() == DARK)
				.count());

		assertEquals(totalNumberOfSquares / 2, squares.stream()
				.filter(square -> square.getType() == LIGHT)
				.count());

		assertEquals(TOTAL_PIECES_PER_PLAYER, result.getBlackPieces().size());
		assertEquals(TOTAL_PIECES_PER_PLAYER, result.getRedPieces().size());

		assertTrue(squares.stream()
				.filter(square -> square.getPosition().equals(new Position(0, 0)) ||
						square.getPosition().equals(new Position(7, 7)))
				.allMatch(square -> square.getType() == LIGHT));
	}

	@Test
	void getStartingBoardHasSquaresDistributedCorrectly() {
		Board result = classUnderTest.getStartingBoard();
		Set<Square> squares = result.getSquares();

		assertTrue(squares.stream()
				.filter(square -> square.getPosition().equals(new Position(0, 0)) ||
						square.getPosition().equals(new Position(7, 7)))
				.allMatch(square -> square.getType() == LIGHT));

		for (int x = 0; x < ROWS; x++) {
			for (int y = 0; y < COLUMNS; y++) {
				if (x % 2 == 0) {
					if (y % 2 == 0) {
						assertEquals(LIGHT, getSquare(squares, x, y).getType());
					} else {
						assertEquals(DARK, getSquare(squares, x, y).getType());
					}
				} else {
					if (y % 2 == 0) {
						assertEquals(DARK, getSquare(squares, x, y).getType());
					} else {
						assertEquals(LIGHT, getSquare(squares, x, y).getType());
					}
				}
			}
		}
	}

	private Square getSquare(Set<Square> squares, int x, int y) {
		return squares.stream()
				.filter(square -> square.getPosition().equals(new Position(x, y)))
				.findFirst()
				.orElseThrow();
	}

	@Test
	void getStartingBoardHasPiecesPutCorrectly() {
		Board result = classUnderTest.getStartingBoard();
		Set<Square> squares = result.getSquares();

		assertTrue(squares.stream()
				.filter(square -> square.getType() == DARK)
				.filter(square -> square.getPosition().getX() == (0) ||
						square.getPosition().getX() == (1) ||
						square.getPosition().getX() == (2)
				)
				.allMatch(square -> square.getDisk() != null && square.getDisk().getColor() == RED));

		assertTrue(squares.stream()
				.filter(square -> square.getType() == DARK)
				.filter(square -> square.getPosition().getX() == (3) ||
						square.getPosition().getX() == (4))
				.allMatch(square -> square.getDisk() == null));


		assertTrue(squares.stream()
				.filter(square -> square.getType() == DARK)
				.filter(square -> square.getPosition().getX() == (7) ||
						square.getPosition().getX() == (6) ||
						square.getPosition().getX() == (5)
				)
				.allMatch(square -> square.getDisk() != null && square.getDisk().getColor() == BLACK));
	}

	@Test
	void getMovesForStartingTurnBlack() {
		Board board = classUnderTest.getStartingBoard();

		Set<Move> result = classUnderTest.getMoves(board, BLACK);
		assertEquals(7, result.size());
	}

	@Test
	void getMovesForStartingTurnRed() {
		Board board = classUnderTest.getStartingBoard();

		Set<Move> result = classUnderTest.getMoves(board, BLACK);
		assertEquals(7, result.size());
	}

	@Test
	void getMovesForRedKing() {
		Board board = classUnderTest.getStartingBoard();
		board.getSquares()
				.forEach(square -> square.setDisk(null));
		Position position = new Position(3, 2);
		Square square = board.getSquare(position)
				.orElseThrow();

		Disk kingDisk = new Disk();
		kingDisk.setColor(RED);
		kingDisk.setKing(true);
		kingDisk.setCurrentPosition(position);
		square.setDisk(kingDisk);

		Set<Move> result = classUnderTest.getMoves(board, RED);
		assertEquals(4, result.size());
	}

	@Test
	void getMovesForBlackKing() {
		Board board = classUnderTest.getStartingBoard();
		board.getSquares()
				.forEach(square -> square.setDisk(null));
		Position position = new Position(3, 2);
		Square square = board.getSquare(position)
				.orElseThrow();

		Disk kingDisk = new Disk();
		kingDisk.setColor(BLACK);
		kingDisk.setKing(true);
		kingDisk.setCurrentPosition(position);
		square.setDisk(kingDisk);

		Set<Move> result = classUnderTest.getMoves(board, BLACK);
		assertEquals(4, result.size());
	}

	@Test
	void getMovesForBlackDiskOneJumpPossible() {
		Board board = classUnderTest.getStartingBoard();
		board.getSquares()
				.forEach(square -> square.setDisk(null));
		Position blackPosition = new Position(5, 2);
		Position redPosition = new Position(4, 1);
		Square squareOne = board.getSquare(blackPosition)
				.orElseThrow();

		Square squareTwo = board.getSquare(redPosition)
				.orElseThrow();

		Disk blackDisk = new Disk();
		blackDisk.setColor(BLACK);
		blackDisk.setCurrentPosition(blackPosition);
		squareOne.setDisk(blackDisk);

		Disk redDisk = new Disk();
		redDisk.setColor(RED);
		redDisk.setCurrentPosition(redPosition);
		squareTwo.setDisk(redDisk);

		Set<Move> result = classUnderTest.getMoves(board, BLACK);
		assertEquals(1, result.size());
		result.stream()
				.findFirst()
				.ifPresent(move -> assertEquals(move.getType(), JUMP));
	}

	@Test
	void getMovesForBlackDiskTwoSlidesPossible() {
		Board board = classUnderTest.getStartingBoard();
		board.getSquares()
				.forEach(square -> square.setDisk(null));
		Position blackPosition = new Position(5, 2);
		Position redPosition = new Position(3, 2);
		Square squareOne = board.getSquare(blackPosition)
				.orElseThrow();

		Square squareTwo = board.getSquare(redPosition)
				.orElseThrow();

		Disk blackDisk = new Disk();
		blackDisk.setColor(BLACK);
		blackDisk.setCurrentPosition(blackPosition);
		squareOne.setDisk(blackDisk);

		Disk redDisk = new Disk();
		redDisk.setColor(RED);
		redDisk.setCurrentPosition(redPosition);
		squareTwo.setDisk(redDisk);

		Set<Move> result = classUnderTest.getMoves(board, BLACK);
		assertEquals(2, result.size());
		assertTrue(result.stream()
				.allMatch(move -> move.getType() == SLIDE));
	}

	@Test
	void getMovesForPieceSurroundedByOpponents() {
		Board board = classUnderTest.getStartingBoard();
		board.getSquares()
				.forEach(square -> square.setDisk(null));
		Position blackPosition = new Position(1, 0);

		Position redPosition1 = new Position(0, 1);
		Position redPosition2 = new Position(2, 1);
		Square squareOne = board.getSquare(blackPosition)
				.orElseThrow();

		Square squareTwo = board.getSquare(redPosition1)
				.orElseThrow();

		Square squareThree = board.getSquare(redPosition2)
				.orElseThrow();

		Disk blackDisk = new Disk();
		blackDisk.setColor(BLACK);
		blackDisk.setCurrentPosition(blackPosition);
		squareOne.setDisk(blackDisk);

		Disk redDisk1 = new Disk();
		redDisk1.setColor(RED);
		redDisk1.setCurrentPosition(redPosition1);
		squareTwo.setDisk(redDisk1);

		Disk redDisk2 = new Disk();
		redDisk2.setColor(RED);
		redDisk2.setCurrentPosition(redPosition2);
		squareThree.setDisk(redDisk2);

		Set<Move> result = classUnderTest.getMoves(board, BLACK);
		assertTrue(result.isEmpty());
	}

	@Test
	void getMovesForKingPieceSurroundedByOpponents() {
		Board board = classUnderTest.getStartingBoard();
		board.getSquares()
				.forEach(square -> square.setDisk(null));
		Position blackPosition = new Position(1, 0);

		Position redPosition1 = new Position(0, 1);
		Position redPosition2 = new Position(2, 1);
		Square squareOne = board.getSquare(blackPosition)
				.orElseThrow();

		Square squareTwo = board.getSquare(redPosition1)
				.orElseThrow();

		Square squareThree = board.getSquare(redPosition2)
				.orElseThrow();

		Disk blackDisk = new Disk();
		blackDisk.setKing(true);
		blackDisk.setColor(BLACK);
		blackDisk.setCurrentPosition(blackPosition);
		squareOne.setDisk(blackDisk);

		Disk redDisk1 = new Disk();
		redDisk1.setColor(RED);
		redDisk1.setCurrentPosition(redPosition1);
		squareTwo.setDisk(redDisk1);

		Disk redDisk2 = new Disk();
		redDisk2.setColor(RED);
		redDisk2.setCurrentPosition(redPosition2);
		squareThree.setDisk(redDisk2);

		Set<Move> result = classUnderTest.getMoves(board, BLACK);
		assertEquals(1, result.size());
		assertTrue(result.stream()
				.allMatch(move -> move.getType() == JUMP));
		result.stream()
				.findFirst()
				.ifPresent(move -> assertEquals(move.getCapturedDisk().getCurrentPosition(), redDisk2.getCurrentPosition()));
	}
}
