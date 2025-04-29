package com.fitness.userService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitness.userService.dto.RegisterRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.model.Users;
import com.fitness.userService.repository.UserRepository;


@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	public UserResponse getUserById(String userId) {
		Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		UserResponse userResponse = new UserResponse();
		userResponse.setId(user.getId());
		userResponse.setKeycloakId(user.getKeycloakId());
		userResponse.setFirstName(user.getFirstName());
		userResponse.setLastName(user.getLastName());
		userResponse.setEmail(user.getEmail());
		userResponse.setCreatedAt(user.getCreatedAt());
		userResponse.setUpdatedAt(user.getUpdatedAt());
		userResponse.setPassword(user.getPassword());
		
		return userResponse;
	}

	public UserResponse registerUser(RegisterRequest request) {
		if(userRepository.existsByEmail(request.getEmail())) {
			 Users existingUser = userRepository.findByEmail(request.getEmail());
	            UserResponse userResponse = new UserResponse();
	            userResponse.setId(existingUser.getId());
	            userResponse.setKeycloakId(existingUser.getKeycloakId());
	            userResponse.setPassword(existingUser.getPassword());
	            userResponse.setEmail(existingUser.getEmail());
	            userResponse.setFirstName(existingUser.getFirstName());
	            userResponse.setLastName(existingUser.getLastName());
	            userResponse.setCreatedAt(existingUser.getCreatedAt());
	            userResponse.setUpdatedAt(existingUser.getUpdatedAt());
	            return userResponse;
		}
		
		Users user = new Users();
		
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setKeycloakId(request.getKeycloakId());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		
		Users saveuser = userRepository.save(user);
		
		UserResponse userResponse = new UserResponse();
		userResponse.setId(saveuser.getId());
		userResponse.setKeycloakId(saveuser.getKeycloakId());
		userResponse.setFirstName(saveuser.getFirstName());
		userResponse.setLastName(saveuser.getLastName());
		userResponse.setEmail(saveuser.getEmail());
		userResponse.setCreatedAt(saveuser.getCreatedAt());
		userResponse.setUpdatedAt(saveuser.getUpdatedAt());
		userResponse.setPassword(saveuser.getPassword());
		userResponse.setCreatedAt(saveuser.getCreatedAt());
		userResponse.setUpdatedAt(saveuser.getUpdatedAt());
		
		return userResponse;
	}

	public Boolean validateUserById(String userId) {
		return userRepository.existsByKeycloakId(userId);
	}
	

}
