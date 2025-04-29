package com.fintness.activityservice.service;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fintness.activityservice.dto.ActivityRequest;
import com.fintness.activityservice.dto.ActivityResponse;
import com.fintness.activityservice.model.Activity;
import com.fintness.activityservice.repository.ActivityRepository;

@Service
public class ActivityService {
	
	@Autowired
	private ActivityRepository activityRepository;
	
	@Autowired
	private UserValidationService userValidationService;
	
	@Value("${rabbitmq.exchange.name}")
	private String exchange;
	
	@Value("${rabbitmq.routing.key}")
	private String routingKey;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public ActivityResponse trackActivity(ActivityRequest request) {
		
		boolean isValidUser = userValidationService.validateUserById(request.getUserId());
		if(!isValidUser) {
			throw new RuntimeException("User not found");
		}
		Activity activity = 
				Activity.builder()
				.userId(request.getUserId())
				.type(request.getType())
				.duration(request.getDuration())
				.caloriesBurned(request.getCaloriesBurned())
				.startTime(request.getStartTime())
				.additionalData(request.getAdditionalData())
				.build();
		
		Activity savedActivity = activityRepository.save(activity);
		
		//publish activity to RabbitMQ
		try {
			rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
		}catch(Exception e) {
			throw new RuntimeException("Error publishing activity to RabbitMQ ", e);
		}
		
		return convertToResponse(savedActivity);
	}
	
	private ActivityResponse convertToResponse(Activity activity) {
	
		ActivityResponse response = new ActivityResponse();
		response.setId(activity.getId());
		response.setUserId(activity.getUserId());
		response.setType(activity.getType());
		response.setDuration(activity.getDuration());
		response.setCaloriesBurned(activity.getCaloriesBurned());
		response.setStartTime(activity.getStartTime());
		response.setAdditionalData(activity.getAdditionalData());
		response.setCreatedAt(activity.getCreatedAt());
		response.setUpdatedAt(activity.getUpdatedAt());
		return response;
		
	}

	public List<ActivityResponse> getUserActivity(String userId) {
		List<Activity> activities = activityRepository.findByUserId(userId);
		return activities.stream()
				.map(this::convertToResponse)
				.toList();
	}

	public ActivityResponse getActivitybyId(String activityId) {
		
		return activityRepository.findById(activityId)
				.map(activity -> convertToResponse(activity))
				.orElseThrow(() -> new RuntimeException("Activity not found"));
	}
	
}
