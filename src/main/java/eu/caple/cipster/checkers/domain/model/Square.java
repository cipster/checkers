package eu.caple.cipster.checkers.domain.model;

import lombok.Data;

@Data
public class Square {

	private Type type;
	private Position position;
	private Disk disk;

	public enum Type {
		LIGHT,
		DARK
	}
}
