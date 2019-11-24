package eu.caple.cipster.checkers.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.caple.cipster.checkers.domain.model.Game;
import eu.caple.cipster.checkers.domain.model.Move;
import eu.caple.cipster.checkers.services.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private GameService gameService;


	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	void startGameShouldReturnCreated() throws Exception {

		this.mockMvc.perform(
				post("/api/v1/game").content("{}"))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.board.squares.length()", is(64)))
				.andExpect(jsonPath("$.status", is("ONGOING")))
				.andExpect(jsonPath("$.playedTurns.length()", is(0)))
				.andExpect(jsonPath("$.capturedDisks.length()", is(0)))
				.andExpect(jsonPath("$.currentTurn.color", is("BLACK")))
				.andExpect(jsonPath("$.currentTurn.move").isEmpty())
				.andExpect(jsonPath("$.currentTurn.possibleMoves.length()", is(7)))
		;
	}

	@Test
	void moveDiskWhenTurnIsBlack() throws Exception {
		MvcResult result = this.mockMvc.perform(
				post("/api/v1/game").content("{}"))
				.andReturn();

		Game createdGame = objectMapper.readValue(result.getResponse().getContentAsString(), Game.class);

		Move move = createdGame.getCurrentTurn().getPossibleMoves()
				.stream()
				.findFirst().orElseThrow();

		this.mockMvc.perform(
				post("/api/v1/game/" + createdGame.getId() + "/moves")
						.content(objectMapper.writeValueAsString(move))
						.contentType(MediaType.APPLICATION_JSON)
		)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.board.squares.length()", is(64)))
				.andExpect(jsonPath("$.status", is("ONGOING")))
				.andExpect(jsonPath("$.playedTurns.length()", is(1)))
				.andExpect(jsonPath("$.capturedDisks.length()", is(0)))
				.andExpect(jsonPath("$.currentTurn.color", is("RED")))
				.andExpect(jsonPath("$.playedTurns[0].move").isNotEmpty())
				.andExpect(jsonPath("$.playedTurns[0].move.type", is("SLIDE")))
				.andExpect(jsonPath("$.currentTurn.possibleMoves.length()", is(7)))
		;
	}

	@Test
	void moveDiskWithWrongGameId() throws Exception {
		MvcResult result = this.mockMvc.perform(
				post("/api/v1/game").content("{}"))
				.andReturn();

		Game createdGame = objectMapper.readValue(result.getResponse().getContentAsString(), Game.class);

		Move move = createdGame.getCurrentTurn().getPossibleMoves()
				.stream()
				.findFirst().orElseThrow();

		this.mockMvc.perform(
				post("/api/v1/game/" + UUID.randomUUID() + "/moves")
						.content(objectMapper.writeValueAsString(move))
						.contentType(MediaType.APPLICATION_JSON)
		)
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}

	@Test
	void moveDiskWithBodyMissingDisk() throws Exception {
		MvcResult result = this.mockMvc.perform(
				post("/api/v1/game").content("{}"))
				.andReturn();

		Game createdGame = objectMapper.readValue(result.getResponse().getContentAsString(), Game.class);

		Move move = createdGame.getCurrentTurn().getPossibleMoves()
				.stream()
				.findFirst().orElseThrow();

		move.setDisk(null);

		this.mockMvc.perform(
				post("/api/v1/game/" + createdGame.getId() + "/moves")
						.content(objectMapper.writeValueAsString(move))
						.contentType(MediaType.APPLICATION_JSON)
		)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("disk must not be null")))
		;
	}

	@Test
	void moveDiskWithBodyMissingFrom() throws Exception {
		MvcResult result = this.mockMvc.perform(
				post("/api/v1/game").content("{}"))
				.andReturn();

		Game createdGame = objectMapper.readValue(result.getResponse().getContentAsString(), Game.class);

		Move move = createdGame.getCurrentTurn().getPossibleMoves()
				.stream()
				.findFirst().orElseThrow();

		move.setFrom(null);

		this.mockMvc.perform(
				post("/api/v1/game/" + createdGame.getId() + "/moves")
						.content(objectMapper.writeValueAsString(move))
						.contentType(MediaType.APPLICATION_JSON)
		)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("from must not be null")))
		;
	}

	@Test
	void moveDiskWithBodyMissingDestination() throws Exception {
		MvcResult result = this.mockMvc.perform(
				post("/api/v1/game").content("{}"))
				.andReturn();

		Game createdGame = objectMapper.readValue(result.getResponse().getContentAsString(), Game.class);

		Move move = createdGame.getCurrentTurn().getPossibleMoves()
				.stream()
				.findFirst().orElseThrow();

		move.setDestination(null);

		this.mockMvc.perform(
				post("/api/v1/game/" + createdGame.getId() + "/moves")
						.content(objectMapper.writeValueAsString(move))
						.contentType(MediaType.APPLICATION_JSON)
		)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("destination must not be null")))
		;
	}
}
