package com.fintness.activityservice.dto;

import java.time.LocalDateTime;
import java.util.Map;
import com.fintness.activityservice.model.ActivityType;

import lombok.Data;

@Data
public class ActivityResponse {
	
	private String id;
	private String userId;
	private ActivityType type;
	private Integer duration;
	private Integer caloriesBurned;
	private LocalDateTime startTime;
	private Map<String, Object> additionalData;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
