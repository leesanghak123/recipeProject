package com.sang.recipe.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {
	
	@CrossOrigin(origins = "http://localhost:8003")
	@GetMapping("/")
	public String home() {
		return "안녕 home";
	}
	
	@GetMapping("/api")
	public String api() {
		return "안녕 api";
	}
}