package com.fitness.aiservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitness.aiservice.model.Recommendations;
import com.fitness.aiservice.repository.AiServiceRepository;

@Service
public class AiService {
	
	@Autowired
	private AiServiceRepository aiServiceRepository;

	public List<Recommendations> getUserRecommendations(String userId) {
		List<Recommendations> userRecommendation = aiServiceRepository.findByUserId(userId);
		return userRecommendation;
	}

	public Recommendations getActivityRecommendations(String activityId) {
		 Recommendations activityRecommendation = aiServiceRepository.findByActivityId(activityId).
				orElseThrow(()-> new RuntimeException("Activity not found "+activityId));
		return activityRecommendation;
	}

}
