package com.fitness.aiservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendations;
import com.fitness.aiservice.repository.AiServiceRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ActivityMessageListener {
	@Autowired
	private ActivityAiService activityAiService;
	
	@Autowired
	private AiServiceRepository aiServiceRepository;
	
	@RabbitListener(queues = "${rabbitmq.queue.name}")
	public void processActivityMessage(Activity activity) {
	log.info("Processing activity message: {}", activity.getId());
//	log.info("Generated recommendation: {}", activityAiService.getRecommendation(activity));
	Recommendations recommendation = activityAiService.getRecommendation(activity);
	
	aiServiceRepository.save(recommendation);
	}
	
	

}
