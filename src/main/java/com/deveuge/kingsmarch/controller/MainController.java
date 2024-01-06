package com.deveuge.kingsmarch.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deveuge.kingsmarch.engine.Game;
import com.deveuge.kingsmarch.engine.Player;
import com.deveuge.kingsmarch.engine.Position;
import com.deveuge.kingsmarch.engine.types.Colour;

@Controller("/")
public class MainController {
	
	@Autowired
	@Qualifier("singleplayerGame")
	Game game;

	/**
	 * Index view
	 * 
	 * @param model {@link Model} Container that holds the data of the application
	 * @param error {@link Optional}<{@link String}> Error message to be displayed
	 * @return {@link String} The main page view
	 */
	@GetMapping
	public String index(Model model, @RequestParam(required = false) Optional<String> error) {
		model.addAttribute("error", error.isPresent() ? error.get() : "");
		return "index";
	}
	
	@PostMapping("move")
	public @ResponseBody String move(String source, String target, Colour colour) {
		Player player = game.getPlayer(colour);
		
		boolean moveCorrect = game.move(player, new Position(source), new Position(target));
		return moveCorrect ? "ok" : "snapback";
	}
	
	
}
