package com.fitness.aiservice.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {
	
	private WebClient webClient;
	
	@Value("${gemini.api.url}")
	private String url ;
	
	@Value("${gemini.api.key}")
	private String apiKey;
	
	public GeminiService (WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}
	
	public String getGeminiData(String question) {
		Map<String, Object> requestBody = Map.of(
				"contents", new Object[] {
						Map.of(
								"parts", new Object[] {
										Map.of("text", question)
								}
						)
				}
		);
		
		String response = webClient.post().uri(url+apiKey)
				.header("Content-Type", "application/json")
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return response;	
	}

}
