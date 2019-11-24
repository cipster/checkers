package eu.caple.cipster.checkers.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class Disk {
	private UUID id;
	private Color color;
	private Position currentPosition;
	private State state = State.IN_PLAY;
	private boolean king;

	public Disk(Color color) {
		this.id = UUID.randomUUID();
		this.color = color;
	}

	public enum Color {
		BLACK,
		RED
	}

	public enum State {
		IN_PLAY,
		CAPTURED
	}
}
