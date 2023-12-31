package com.deveuge.kingsmarch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deveuge.kingsmarch.engine.Game;

@Controller
@RequestMapping("/sp")
public class SingleplayerController {

	@Autowired
	@Qualifier("singleplayerGame")
	Game game;
	
	@GetMapping
	public String game(Model model) {
        model.addAttribute("gameType", "singleplayer");
		return "game";
	}

	@PostMapping("autoMove")
	public @ResponseBody String autoMove() {
		return "g8-f6";
	}
}
