package com.abhishek.studentms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Controller
public class HomeController {

	// Opening the deployed URL lands on the interactive API docs
	@GetMapping("/")
	public String home() {
		return "redirect:/swagger-ui.html";
	}

}
