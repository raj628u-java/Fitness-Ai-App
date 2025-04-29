package com.apigateway.main.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserService {

	@Autowired
	private WebClient userServicewebClient;
	
	public Mono<Boolean> validateUserById(String userId) {
		log.info("Calling User Validation API for userId: {}", userId);
		return userServicewebClient.get()
				.uri("/api/v1/user/{userId}/validate", userId)
				.retrieve()
				.bodyToMono(Boolean.class)
				.onErrorResume(WebClientResponseException.class, e -> {
					if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
						return Mono.just(false);
					} else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
						return Mono.error(new RuntimeException("User service is down"));
					} else {
						return Mono.error(new RuntimeException("Invalid Request" + userId));
					}
				});
	}
	
	public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
		log.info("Registering User with ID: {}", registerRequest.getKeycloakId());
		return userServicewebClient.post()
				.uri("/api/v1/user/register")
				.bodyValue(registerRequest)
				.retrieve()
				.bodyToMono(UserResponse.class)
				.onErrorResume(WebClientResponseException.class, e -> {
					if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
						return Mono.error(new RuntimeException("Invalid Request"+e.getMessage()));
					} else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
						return Mono.error(new RuntimeException("Internal Server Error"+e.getMessage()));
					}else {
						return Mono.error(new RuntimeException("Error while registering user"+e.getMessage()));
					}
				});
	}
}
	
