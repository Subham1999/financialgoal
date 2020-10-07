package com.subham.financialgoal.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Testcontroller {
	@GetMapping("/")
	public String test() {
		return "<h1>Subham Santra</h1>";
	}
}
