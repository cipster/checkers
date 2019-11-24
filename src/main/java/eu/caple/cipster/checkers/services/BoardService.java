package eu.caple.cipster.checkers.services;

import eu.caple.cipster.checkers.domain.model.Board;
import eu.caple.cipster.checkers.domain.model.Disk;
import eu.caple.cipster.checkers.domain.model.Move;
import eu.caple.cipster.checkers.domain.model.Position;
import eu.caple.cipster.checkers.domain.model.Square;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static eu.caple.cipster.checkers.domain.model.Disk.Color.BLACK;
import static eu.caple.cipster.checkers.domain.model.Disk.Color.RED;
import static eu.caple.cipster.checkers.domain.model.Disk.State.CAPTURED;
import static eu.caple.cipster.checkers.domain.model.Move.Type.JUMP;
import static eu.caple.cipster.checkers.domain.model.Move.Type.SLIDE;
import static eu.caple.cipster.checkers.domain.model.Square.Type.DARK;
import static eu.caple.cipster.checkers.domain.model.Square.Type.LIGHT;
import static eu.caple.cipster.checkers.services.GameService.COLUMNS;
import static eu.caple.cipster.checkers.services.GameService.ROWS;
import static eu.caple.cipster.checkers.services.GameService.TOTAL_PIECES_PER_PLAYER;
import static java.util.Objects.isNull;

@Service
public class BoardService {

	private final Comparator<Square> squareXComparator = (square, nextSquare) -> {
		Position firstPosition = square.getPosition();
		Position secondPosition = nextSquare.getPosition();
		return firstPosition.getX().compareTo(secondPosition.getX());
	};

	private final Comparator<Square> squareYComparator = (square, nextSquare) -> {
		Position firstPosition = square.getPosition();
		Position secondPosition = nextSquare.getPosition();
		return firstPosition.getY().compareTo(secondPosition.getY());
	};

	public Board getStartingBoard() {
		Board board = new Board();
		board.setSquares(initSquares());
		Map<Disk.Color, List<Disk>> pieces = initPieces();

		board.setBlackPieces(pieces.get(BLACK));
		board.setRedPieces(pieces.get(RED));

		layBlackPieces(board);
		layRedPieces(board);

		return board;
	}

	public Set<Move> getMoves(Board board, Disk.Color color) {
		Set<Move> allMoves = board.getSquares().stream()
				.map(Square::getDisk)
				.filter(Objects::nonNull)
				.filter(disk -> Objects.equals(disk.getColor(), color))
				.filter(disk -> disk.getState() != CAPTURED)
				.flatMap(disk -> getMoves(disk, board).stream())
				.collect(Collectors.toSet());
		Set<Move> jumpMoves = allMoves.stream()
				.filter(move -> move.getType() == JUMP)
				.collect(Collectors.toSet());

		if (jumpMoves.isEmpty()) {
			return allMoves;
		} else {
			return jumpMoves;
		}
	}

	private void layBlackPieces(Board board) {
		int numberOfFilledRowsPerColor = getNumberOfFilledRowsPerColor();
		List<Square> darkSquares = getDarkSquares(board);
		darkSquares.sort((a, b) -> b.getPosition().getX().compareTo(a.getPosition().getX()));

		List<Square> squaresToFill = darkSquares.stream()
				.filter(square -> square.getPosition().getX() >= ROWS - numberOfFilledRowsPerColor)
				.collect(Collectors.toList());

		for (int i = 0; i < squaresToFill.size(); i++) {
			Disk disk = board.getBlackPieces().get(i);
			Square square = squaresToFill.get(i);
			disk.setCurrentPosition(square.getPosition());
			square.setDisk(disk);
		}
	}

	private void layRedPieces(Board board) {
		int numberOfFilledRowsPerColor = getNumberOfFilledRowsPerColor();
		List<Square> squareToFill = getDarkSquares(board).stream()
				.filter(square -> square.getPosition().getX() < numberOfFilledRowsPerColor)
				.collect(Collectors.toList());

		for (int i = 0; i < squareToFill.size(); i++) {
			Disk disk = board.getRedPieces().get(i);
			Square square = squareToFill.get(i);
			disk.setCurrentPosition(square.getPosition());
			square.setDisk(disk);
		}
	}

	private Set<Square> initSquares() {
		Set<Square> squares = new TreeSet<>(squareXComparator.thenComparing(squareYComparator));
		for (int x = 0; x < ROWS; x++) {
			for (int y = 0; y < COLUMNS; y++) {
				Square square = new Square();
				square.setPosition(new Position(x, y));
				square.setType(getSquareType(x, y));
				squares.add(square);
			}
		}

		return squares;
	}

	private Map<Disk.Color, List<Disk>> initPieces() {
		List<Disk> blackPieces = new ArrayList<>(TOTAL_PIECES_PER_PLAYER);
		List<Disk> redPieces = new ArrayList<>(TOTAL_PIECES_PER_PLAYER);
		for (int i = 0; i < TOTAL_PIECES_PER_PLAYER; i++) {
			Disk blackDisk = new Disk(BLACK);
			Disk redDisk = new Disk(RED);
			blackPieces.add(blackDisk);
			redPieces.add(redDisk);
		}

		return Map.of(BLACK, blackPieces, RED, redPieces);
	}

	private Square.Type getSquareType(int x, int y) {
		if (x % 2 == 0) {
			if (y % 2 == 0) {
				return LIGHT;
			} else {
				return DARK;
			}
		} else {
			if (y % 2 == 0) {
				return DARK;
			} else {
				return LIGHT;
			}
		}
	}

	private List<Square> getDarkSquares(Board board) {
		return board.getSquares().stream()
				.filter(square -> square.getType() == DARK)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private int getNumberOfFilledRowsPerColor() {
		return TOTAL_PIECES_PER_PLAYER / (COLUMNS / 2);
	}


	private Set<Move> getMoves(Disk disk, Board board) {
		Set<Move> moves = new HashSet<>();
		Position currentPosition = disk.getCurrentPosition();
		int diskX = currentPosition.getX();
		int diskY = currentPosition.getY();
		Disk.Color playerColor = disk.getColor();

		Position forwardLeft;
		Position forwardRight;
		Position forwardJumpLeft;
		Position forwardJumpRight;

		if (playerColor == Disk.Color.RED) {
			forwardLeft = new Position(diskX + 1, diskY - 1);
			forwardRight = new Position(diskX + 1, diskY + 1);
			forwardJumpLeft = new Position(diskX + 2, diskY - 2);
			forwardJumpRight = new Position(diskX + 2, diskY + 2);
		} else {
			forwardLeft = new Position(diskX - 1, diskY - 1);
			forwardRight = new Position(diskX - 1, diskY + 1);
			forwardJumpLeft = new Position(diskX - 2, diskY - 2);
			forwardJumpRight = new Position(diskX - 2, diskY + 2);
		}
		board.getSquare(forwardLeft)
				.ifPresent(square -> moves.addAll(getMove(disk, board, currentPosition, playerColor, forwardLeft, forwardJumpLeft, square)));
		board.getSquare(forwardRight)
				.ifPresent(square -> moves.addAll(getMove(disk, board, currentPosition, playerColor, forwardRight, forwardJumpRight, square)));

		if (disk.isKing()) {
			Position backwardsLeft;
			Position backwardsRight;
			Position backwardsJumpLeft;
			Position backwardsJumpRight;

			if (playerColor == Disk.Color.RED) {
				backwardsLeft = new Position(diskX - 1, diskY - 1);
				backwardsRight = new Position(diskX - 1, diskY + 1);
				backwardsJumpLeft = new Position(diskX - 2, diskY - 2);
				backwardsJumpRight = new Position(diskX - 2, diskY + 2);
			} else {
				backwardsLeft = new Position(diskX + 1, diskY - 1);
				backwardsRight = new Position(diskX + 1, diskY + 1);
				backwardsJumpLeft = new Position(diskX + 2, diskY - 2);
				backwardsJumpRight = new Position(diskX + 2, diskY + 2);
			}

			board.getSquare(backwardsLeft)
					.ifPresent(square -> moves.addAll(getMove(disk, board, currentPosition, playerColor, backwardsLeft, backwardsJumpLeft, square)));
			board.getSquare(backwardsRight)
					.ifPresent(square -> moves.addAll(getMove(disk, board, currentPosition, playerColor, backwardsRight, backwardsJumpRight, square)));
		}

		return moves;
	}

	private Set<Move> getMove(Disk disk, Board board, Position currentPosition, Disk.Color playerColor, Position nextSquare, Position jumpSquare, Square square) {
		Set<Move> moves = new HashSet<>();
		Move move = new Move();
		move.setDisk(disk);
		move.setFrom(currentPosition);
		Disk foundDisk = square.getDisk();

		if (foundDisk == null || foundDisk.getState() == CAPTURED) {
			move.setDestination(nextSquare);
			move.setType(SLIDE);
			moves.add(move);
		} else {
			if (foundDisk.getColor() != playerColor) {
				Optional<Square> possibleLandingSquare = board.getSquare(jumpSquare);
				possibleLandingSquare.ifPresent(landingSquare -> {
					if (isNull(landingSquare.getDisk()) || landingSquare.getDisk().getState() == CAPTURED) {
						move.setDestination(jumpSquare);
						move.setCapturedDisk(foundDisk);
						move.setType(JUMP);
						moves.add(move);
					}
				});
			}
		}
		return moves;
	}

}
