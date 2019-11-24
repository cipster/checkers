package eu.caple.cipster.checkers.controllers;

import eu.caple.cipster.checkers.domain.model.Game;
import eu.caple.cipster.checkers.domain.model.Move;
import eu.caple.cipster.checkers.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {

	private final GameService gameService;

	@PostMapping
	public ResponseEntity startGame() {
		Game newGame = gameService.startGame();

		return ResponseEntity
				.status(CREATED)
				.body(newGame);
	}

	@PostMapping("{gameId}/moves")
	public ResponseEntity moveDisk(@PathVariable("gameId") UUID gameId, @RequestBody @Valid Move move) {
		Game game = gameService.moveDisk(gameId, move);

		return ResponseEntity
				.ok(game);
	}
}
