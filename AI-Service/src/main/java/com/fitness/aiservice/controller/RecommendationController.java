package com.fitness.aiservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.aiservice.model.Recommendations;
import com.fitness.aiservice.service.AiService;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
	
	@Autowired
	private AiService aiService;

	
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Recommendations>> getUserRecommendations(@PathVariable String userId) {
		// Logic to get recommendations for the user
		List<Recommendations> recommendations = aiService.getUserRecommendations(userId);
		return ResponseEntity.ok(recommendations);
	}
	
	@GetMapping("/activity/{activityId}")
	public ResponseEntity<Recommendations> getActivityRecommendations(@PathVariable String activityId) {
		// Logic to get recommendations for the user
		Recommendations recommendations = aiService.getActivityRecommendations(activityId);
		return ResponseEntity.ok(recommendations);
	}
}
