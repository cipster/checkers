package eu.caple.cipster.checkers.services;

import eu.caple.cipster.checkers.domain.BoardException;
import eu.caple.cipster.checkers.domain.GameException;
import eu.caple.cipster.checkers.domain.model.Board;
import eu.caple.cipster.checkers.domain.model.Disk;
import eu.caple.cipster.checkers.domain.model.Game;
import eu.caple.cipster.checkers.domain.model.Move;
import eu.caple.cipster.checkers.domain.model.Position;
import eu.caple.cipster.checkers.domain.model.Square;
import eu.caple.cipster.checkers.domain.model.Turn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static eu.caple.cipster.checkers.domain.model.Disk.Color.BLACK;
import static eu.caple.cipster.checkers.domain.model.Disk.Color.RED;
import static eu.caple.cipster.checkers.domain.model.Game.Status.FINISHED;
import static eu.caple.cipster.checkers.domain.model.Move.Type.JUMP;
import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class GameService {
	static final int ROWS = 8;
	static final int COLUMNS = 8;
	static final int TOTAL_PIECES_PER_PLAYER = 12;
	private final BoardService boardService;

	private Set<Game> games = new HashSet<>();

	public Game startGame() {
		Board board = boardService.getStartingBoard();
		Game game = new Game();
		Turn firstTurn = new Turn();
		firstTurn.setColor(BLACK);
		firstTurn.setPossibleMoves(getMoves(board, BLACK));
		game.setCurrentTurn(firstTurn);
		game.setBoard(board);

		games.add(game);

		return game;
	}

	private Set<Move> getMoves(Board board, Disk.Color color) {
		return boardService.getMoves(board, color);
	}

	public Game moveDisk(UUID gameId, Move move) {
		Game game = getGame(gameId);
		Board board = game.getBoard();
		Turn currentTurn = game.getCurrentTurn();
		Disk.Color currentPlayerColor = currentTurn.getColor();

		Move currentMove = getMoves(board, currentPlayerColor)
				.stream()
				.filter(possibleMove -> Objects.equals(possibleMove.getDisk().getColor(), move.getDisk().getColor()))
				.filter(possibleMove -> Objects.equals(possibleMove.getDisk().getId(), move.getDisk().getId()))
				.filter(possibleMove -> Objects.equals(possibleMove.getFrom(), move.getFrom()))
				.filter(possibleMove -> Objects.equals(possibleMove.getDestination(), move.getDestination()))
				.findFirst()
				.orElseThrow(() -> new GameException("Move is not permitted"));

		Square fromSquare = board.getSquare(currentMove.getFrom())
				.orElseThrow(() -> new BoardException("No such square"));
		Position destination = currentMove.getDestination();

		Square destinationSquare = board.getSquare(destination)
				.orElseThrow(() -> new BoardException("No such square"));
		Disk capturedDisk = currentMove.getCapturedDisk();
		if (capturedDisk != null) {
			capturedDisk.setState(Disk.State.CAPTURED);
			game.getCapturedDisks().add(capturedDisk);
		}

		fromSquare.setDisk(null);

		Disk disk = currentMove.getDisk();
		disk.setCurrentPosition(destination);
		destinationSquare.setDisk(disk);

		if (isEndOfBoard(destination, currentPlayerColor)) {
			disk.setKing(true);
		}

		currentTurn.setMove(currentMove);
		game.getPlayedTurns().add(currentTurn);

		Set<Move> nextPossibleMovesForCurrentPlayer = getMoves(board, currentPlayerColor);

		Turn nextTurn;
		if (currentMove.getType() == JUMP && diskHasMoreJumps(disk, nextPossibleMovesForCurrentPlayer)) {
			nextTurn = new Turn();
			nextTurn.setColor(currentPlayerColor);
			nextTurn.setPossibleMoves(nextPossibleMovesForCurrentPlayer);
		} else {
			nextTurn = getTurnForNextPlayer(currentTurn);
			Set<Move> possibleMoves = getMoves(board, nextTurn.getColor());

			if (possibleMoves.isEmpty()) {
				game.setStatus(FINISHED);
				nextTurn.setPossibleMoves(Set.of());
			} else {
				nextTurn.setPossibleMoves(possibleMoves);
			}
		}
		game.setCurrentTurn(nextTurn);

		return game;
	}

	private boolean diskHasMoreJumps(Disk disk, Set<Move> nextPossibleMovesForCurrentPlayer) {
		return nextPossibleMovesForCurrentPlayer.stream()
				.filter(m -> m.getType() == JUMP)
				.anyMatch(m -> Objects.equals(m.getDisk().getId(), disk.getId()));
	}

	private boolean isEndOfBoard(Position position, Disk.Color color) {
		if (color == RED) {
			return position.getX() == (ROWS - 1);
		} else {
			return position.getX() == 0;
		}
	}

	private Game getGame(UUID gameId) {
		return games.stream()
				.filter(game -> Objects.equals(game.getId(), gameId))
				.findFirst()
				.orElseThrow(() -> new GameException(format("No such game %s", gameId)));
	}

	private Turn getTurnForNextPlayer(Turn turn) {
		Turn nextTurn = new Turn();
		if (turn.getColor() == BLACK) {
			nextTurn.setColor(RED);
		} else {
			nextTurn.setColor(BLACK);
		}

		return nextTurn;
	}
}
