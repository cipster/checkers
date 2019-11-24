package eu.caple.cipster.checkers.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Data
public class Turn {
	private Disk.Color color;
	private Move move;
	private Set<Move> possibleMoves;
}
