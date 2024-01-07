package com.deveuge.kingsmarch.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("/")
public class MainController {
	
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
	
}
