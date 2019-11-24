package eu.caple.cipster.checkers.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static eu.caple.cipster.checkers.domain.model.Game.Status.ONGOING;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Game {

	@EqualsAndHashCode.Include
	private UUID id;

	private Board board;
	private List<Turn> playedTurns;
	private List<Disk> capturedDisks;
	private Turn currentTurn;
	private Status status;

	public Game() {
		this.id = UUID.randomUUID();
		this.status = ONGOING;
		this.playedTurns = new ArrayList<>();
		this.capturedDisks = new ArrayList<>();
	}

	public enum Status {
		ONGOING,
		FINISHED
	}
}
