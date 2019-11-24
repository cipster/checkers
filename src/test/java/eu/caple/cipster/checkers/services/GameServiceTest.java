package eu.caple.cipster.checkers.services;

import eu.caple.cipster.checkers.domain.GameException;
import eu.caple.cipster.checkers.domain.model.Board;
import eu.caple.cipster.checkers.domain.model.Disk;
import eu.caple.cipster.checkers.domain.model.Game;
import eu.caple.cipster.checkers.domain.model.Move;
import eu.caple.cipster.checkers.domain.model.Position;
import eu.caple.cipster.checkers.domain.model.Square;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static eu.caple.cipster.checkers.domain.model.Disk.Color.BLACK;
import static eu.caple.cipster.checkers.domain.model.Disk.Color.RED;
import static eu.caple.cipster.checkers.domain.model.Move.Type.JUMP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@Mock
	private BoardService boardService;

	@InjectMocks
	private GameService classUnderTest;

	@Mock
	private Move move;
	@Mock
	private Board board;
	@Mock
	private Square square;


	@Test
	void startGameHasBlackAsPlayerOne() {
		Game result = classUnderTest.startGame();

		assertEquals(result.getCurrentTurn().getColor(), BLACK);
	}

	@Test
	void startGameGetsPossibleMovesFromBoardService() {
		when(boardService.getStartingBoard()).thenReturn(board);

		classUnderTest.startGame();

		verify(boardService).getMoves(any(Board.class), any(Disk.Color.class));
	}

	@Test
	void moveDiskWithWrongId() {
		assertThrows(GameException.class, () -> classUnderTest.moveDisk(UUID.randomUUID(), move));
	}

	@Test
	void moveDiskWithInvalidDiskColor() {
		Game game = classUnderTest.startGame();
		Move invalidMove = new Move();
		Disk disk = new Disk(RED);
		invalidMove.setDisk(disk);

		assertThrows(GameException.class, () -> classUnderTest.moveDisk(game.getId(), invalidMove));
	}

	@Test
	void moveDiskWithInvalidDiskId() {
		Game game = classUnderTest.startGame();
		Move invalidMove = new Move();
		Disk disk = new Disk(BLACK);
		invalidMove.setDisk(disk);

		assertThrows(GameException.class, () -> classUnderTest.moveDisk(game.getId(), invalidMove));
	}

	@Test
	void moveDiskWithInvalidMoveFrom() {
		when(boardService.getStartingBoard()).thenReturn(board);
		Game game = classUnderTest.startGame();
		Disk disk = new Disk(BLACK);

		when(move.getFrom()).thenReturn(new Position(1, 1));
		when(move.getDisk()).thenReturn(disk);
		when(boardService.getMoves(any(Board.class), any(Disk.Color.class))).thenReturn(Set.of(move));

		Move invalidMove = new Move();
		invalidMove.setDisk(disk);
		invalidMove.setFrom(new Position(-1, -1));

		assertThrows(GameException.class, () -> classUnderTest.moveDisk(game.getId(), invalidMove));
	}

	@Test
	void moveDiskWithInvalidMoveDestination() {
		when(boardService.getStartingBoard()).thenReturn(board);
		Game game = classUnderTest.startGame();
		Disk disk = new Disk(BLACK);

		Position from = new Position(1, 1);
		when(move.getFrom()).thenReturn(from);
		when(move.getDestination()).thenReturn(new Position(2, 2));
		when(move.getDisk()).thenReturn(disk);
		when(boardService.getMoves(any(Board.class), any(Disk.Color.class))).thenReturn(Set.of(move));

		Move invalidMove = new Move();
		invalidMove.setDisk(disk);
		invalidMove.setFrom(from);
		invalidMove.setDestination(new Position(-1, -1));

		assertThrows(GameException.class, () -> classUnderTest.moveDisk(game.getId(), invalidMove));
	}

	@Test
	void moveDiskNoJumpInvolved() {
		when(board.getSquare(any(Position.class))).thenReturn(Optional.of(square));
		when(boardService.getStartingBoard()).thenReturn(board);
		Game game = classUnderTest.startGame();
		Disk disk = new Disk(BLACK);

		when(move.getFrom()).thenReturn(new Position(5, 2));
		when(move.getDestination()).thenReturn(new Position(4, 1));
		when(move.getDisk()).thenReturn(disk);
		when(boardService.getMoves(any(Board.class), any(Disk.Color.class))).thenReturn(Set.of(move));

		Game result = classUnderTest.moveDisk(game.getId(), move);
		assertEquals(RED, result.getCurrentTurn().getColor());
		assertTrue(result.getCapturedDisks().isEmpty());
	}

	@Test
	void moveDiskWithJumpInvolved() {
		when(board.getSquare(any(Position.class))).thenReturn(Optional.of(square));
		when(boardService.getStartingBoard()).thenReturn(board);

		Game game = classUnderTest.startGame();
		Disk blackDisk = new Disk(BLACK);
		Disk redDisk = new Disk(RED);
		Move capturingMove = mock(Move.class);

		when(move.getFrom()).thenReturn(new Position(5, 2));
		when(move.getDestination()).thenReturn(new Position(3, 2));
		when(move.getDisk()).thenReturn(blackDisk);
		when(boardService.getMoves(any(Board.class), any(Disk.Color.class))).thenReturn(Set.of(move));

		classUnderTest.moveDisk(game.getId(), move);

		when(capturingMove.getFrom()).thenReturn(new Position(2, 1));
		when(capturingMove.getDestination()).thenReturn(new Position(4, 3));
		when(capturingMove.getDisk()).thenReturn(redDisk);
		when(capturingMove.getCapturedDisk()).thenReturn(blackDisk);
		when(boardService.getMoves(any(Board.class), any(Disk.Color.class))).thenReturn(Set.of(capturingMove));

		Game result = classUnderTest.moveDisk(game.getId(), capturingMove);
		assertEquals(BLACK, result.getCurrentTurn().getColor());
		List<Disk> capturedDisks = result.getCapturedDisks();
		assertEquals(1, capturedDisks.size());
		assertEquals(blackDisk, capturedDisks.get(0));
	}

	@Test
	void moveDiskToEndOfBoardBecomesKing() {
		Disk blackDisk = new Disk(BLACK);

		when(square.getDisk()).thenReturn(blackDisk);
		when(board.getSquare(any(Position.class))).thenReturn(Optional.of(square));
		when(boardService.getStartingBoard()).thenReturn(board);
		when(move.getFrom()).thenReturn(new Position(7, 0));
		Position endOfBoardPosition = new Position(0, 1);
		when(move.getDestination()).thenReturn(endOfBoardPosition);
		when(move.getDisk()).thenReturn(blackDisk);
		when(boardService.getMoves(any(Board.class), any(Disk.Color.class))).thenReturn(Set.of(move));

		Game game = classUnderTest.startGame();
		Game result = classUnderTest.moveDisk(game.getId(), move);
		assertEquals(RED, result.getCurrentTurn().getColor());
		result.getBoard()
				.getSquare(endOfBoardPosition)
				.ifPresent(s -> assertTrue(s.getDisk().isKing()));
	}

	@Test
	void moveDiskJumpsMultipleTimes() {
		Disk blackDisk = new Disk(BLACK);

		when(board.getSquare(any(Position.class))).thenReturn(Optional.of(square));
		when(boardService.getStartingBoard()).thenReturn(board);
		when(move.getFrom()).thenReturn(new Position(7, 0));
		when(move.getDestination()).thenReturn(new Position(0, 1));
		when(move.getDisk()).thenReturn(blackDisk);
		when(move.getType()).thenReturn(JUMP);
		when(boardService.getMoves(any(Board.class), any(Disk.Color.class))).thenReturn(Set.of(move));

		Game game = classUnderTest.startGame();
		Game result = classUnderTest.moveDisk(game.getId(), move);
		assertEquals(BLACK, result.getCurrentTurn().getColor());
	}

	@Test
	void moveDiskFinishesGameWhenNoMoreMoves() {
		Disk blackDisk = new Disk(BLACK);

		when(board.getSquare(any(Position.class))).thenReturn(Optional.of(square));
		when(boardService.getStartingBoard()).thenReturn(board);
		when(move.getFrom()).thenReturn(new Position(7, 0));
		when(move.getDestination()).thenReturn(new Position(0, 1));
		when(move.getDisk()).thenReturn(blackDisk);
		when(move.getType()).thenReturn(JUMP);
		Game game = classUnderTest.startGame();
		when(boardService.getMoves(any(Board.class), any(Disk.Color.class))).thenReturn(Set.of(move), Set.of());

		Game result = classUnderTest.moveDisk(game.getId(), move);
		assertEquals(Game.Status.FINISHED, result.getStatus());
		assertTrue(result.getCurrentTurn().getPossibleMoves().isEmpty());
	}
}
