package com.deveuge.kingsmarch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.deveuge.kingsmarch.engine.Game;

@Controller("/")
public class MainController {
	
	Game game;

	@GetMapping
	public String init() {
		return "index";
	}
	
}
