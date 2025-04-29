package com.fintness.activityservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fintness.activityservice.model.Activity;

public interface ActivityRepository extends MongoRepository<Activity, String> {

	List<Activity> findByUserId(String userId);
	
	// Custom query methods can be defined here if needed
	// For example, find activities by userId or date range
	// List<Activity> findByUserIdAndStartTimeBetween(String userId, LocalDateTime start, LocalDateTime end);

}
