package com.fitness.aiservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.fitness.aiservice.model.Recommendations;

@Repository
public interface AiServiceRepository extends MongoRepository<Recommendations, String> {
	Optional<Recommendations> findByActivityId(String activityId);

	List<Recommendations> findByUserId(String userId);

}
