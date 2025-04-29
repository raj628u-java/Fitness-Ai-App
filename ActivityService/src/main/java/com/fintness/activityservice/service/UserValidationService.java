package com.fintness.activityservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class UserValidationService {

	@Autowired
	private WebClient userServicewebClient;
	
	public boolean validateUserById(String userId) {
		try {
		return userServicewebClient.get()
				.uri("/api/v1/user/{userId}/validate", userId)
				.retrieve()
				.bodyToMono(Boolean.class)
				.block();
	}catch (WebClientResponseException e) {
			if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("User not found");
			}else if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
					throw new RuntimeException("User service is down");	
			 }else {
				throw new RuntimeException("Invalid Request"+userId);
			}
		}catch (Exception ex) {
			throw new RuntimeException("Error occurred while validating user"+ex.getMessage());
		}
	}
	
}
