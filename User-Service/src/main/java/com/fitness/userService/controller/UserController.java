package com.fitness.userService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.userService.dto.RegisterRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("{userId}")
	public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
		// Logic to get user by ID
		UserResponse user = userService.getUserById(userId);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
		// Logic to register a new user
		UserResponse registeredUser = userService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
	}

	@GetMapping("{userId}/validate")
	public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {
		// Logic to get user by ID
		Boolean validUser = userService.validateUserById(userId);
		System.out.println("Valid User: "+validUser);
		return ResponseEntity.ok(validUser);
	}
	
}
