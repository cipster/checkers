package eu.caple.cipster.checkers.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
public class Move {

	@NotNull
	private Disk disk;

	@EqualsAndHashCode.Exclude
	private Disk capturedDisk;
	private Type type;

	@EqualsAndHashCode.Exclude
	@NotNull
	private Position from;

	@NotNull
	private Position destination;

	public enum Type {
		SLIDE,
		JUMP
	}
}
