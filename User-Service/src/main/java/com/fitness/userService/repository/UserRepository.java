package com.fitness.userService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.model.Users;

public interface UserRepository extends JpaRepository<Users, String> {

	boolean existsByEmail(String email);

	Users findByEmail(String email);

	Boolean existsByKeycloakId(String userId);

}
