package eu.caple.cipster.checkers.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static eu.caple.cipster.checkers.domain.model.Square.Type.DARK;

@Data
public class Board {

	private UUID id;

	@JsonIgnore
	private List<Disk> blackPieces;

	@JsonIgnore
	private List<Disk> redPieces;

	private Set<Square> squares;

	public Board() {
		id = UUID.randomUUID();
	}

	public Optional<Square> getSquare(Position position) {
		return this.squares
				.stream()
				.filter(square -> square.getType() == DARK)
				.filter(square -> Objects.equals(square.getPosition().getX(), position.getX()))
				.filter(square -> Objects.equals(square.getPosition().getY(), position.getY()))
				.findFirst();
	}


}
