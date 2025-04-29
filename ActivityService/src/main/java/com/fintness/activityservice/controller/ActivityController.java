package com.fintness.activityservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintness.activityservice.dto.ActivityRequest;
import com.fintness.activityservice.dto.ActivityResponse;
import com.fintness.activityservice.service.ActivityService;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {
	
	@Autowired
	private ActivityService activityService;
	
	@GetMapping
	public ResponseEntity<List<ActivityResponse>> getuUserActivity(@RequestHeader("X-User-ID") String userId) {
		
		return ResponseEntity.ok(activityService.getUserActivity(userId));
	}
	
	@GetMapping("/{activityId}")
	public ResponseEntity<ActivityResponse> getActivity(@PathVariable String activityId) {
		
		return ResponseEntity.ok(activityService.getActivitybyId(activityId));
	}
	
	@PostMapping
	public ResponseEntity<ActivityResponse> createActivity(@RequestBody ActivityRequest request) {
		
		return ResponseEntity.ok(activityService.trackActivity(request));
	}

}
