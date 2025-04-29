package com.fitness.aiservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendations;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ActivityAiService {

	@Autowired
	private GeminiService geminiService;
	
	public Recommendations getRecommendation(Activity activity) {
		String prompt = createPromptForActivty(activity);
		log.info("Received question: {}", activity);
		String response = geminiService.getGeminiData(prompt);
		log.info("Received response: {}", response);
		return processResponse(response, activity);
	}

	private Recommendations processResponse(String response, Activity activity) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response);
			
			JsonNode recommendationNode = jsonNode.path("candidates")
					.get(0).path("content")
					.path("parts").get(0)
					.path("text");
			
		String recommendation = recommendationNode.asText()
				 .replaceAll("```json\\n","")
                 .replaceAll("\\n```", "")
                 .replaceAll("\n```", "")
                 .trim();
		
		log.info("Parsed response From Gemini AI: {}", recommendation);
		JsonNode recommendationJson = objectMapper.readTree(recommendation);
		JsonNode analysis = recommendationJson.path("analysis");
		StringBuilder fullAnalysis = new StringBuilder();
		addAnalysis(fullAnalysis, analysis, "overall", "Overall:");
		addAnalysis(fullAnalysis, analysis, "pace", "Pace:");
		addAnalysis(fullAnalysis, analysis, "heartRate", "Heart Rate:");
		addAnalysis(fullAnalysis, analysis, "caloriesBurned", "Calories Burned:");
		
		List<String> improvements = extractImprovements(recommendationJson.path("improvements"));
		List<String> suggestions = extractSuggestions(recommendationJson.path("suggestions"));
		List<String> safety = extrctSafetyGuidelines(recommendationJson.path("safety"));
		
		return Recommendations.builder()
				.activityId(activity.getId())
				.userId(activity.getUserId())
				.activityType(activity.getType())
				.recommendation(fullAnalysis.toString().trim())
				.improvements(improvements)
				.suggestions(suggestions)
				.safety(safety)
				.createdAt(LocalDateTime.now())
				.build();
		
		}catch (Exception e) {
			log.error("Error processing response: {}", e.getMessage());
			return createDefaultRecommendation(activity);
		}
		
		
	}

	

	private Recommendations createDefaultRecommendation(Activity activity) {
		// TODO Auto-generated method stub
		return Recommendations.builder()
				.activityId(activity.getId())
				.userId(activity.getUserId())
				.activityType(activity.getType())
				.recommendation("No recommendation available")
				.improvements(Collections.singletonList("No specific Improvements Provided"))
				.suggestions(Collections.singletonList("No specific Suggestions Provided"))
				.safety(Collections.singletonList("Follow General Safety Guidelines"))
				.createdAt(LocalDateTime.now())
				.build();
	}

	private List<String> extrctSafetyGuidelines(JsonNode safetyNode) {
		// TODO Auto-generated method stub
		List<String> safety = new ArrayList<>();
		if(safetyNode.isArray()) {
			for(JsonNode safetyPoint : safetyNode) {
				String point = safetyPoint.asText();
				safety.add(point);
			}
		}
		return safety.isEmpty() ? Collections.singletonList("Follow General SafetyGuidelines") : safety;
	}

	private List<String> extractSuggestions(JsonNode suggestionsNode) {
		List<String> suggestions = new ArrayList<>();
		if(suggestionsNode.isArray()) {
			for(JsonNode suggestion : suggestionsNode) {
				String workout = suggestion.path("workout").asText();
				String description = suggestion.path("description").asText();
				suggestions.add(workout + ": " + description);
			}
		}
		return suggestions.isEmpty() ? Collections.singletonList("No specific Suggestions Provided") : suggestions;
	}

	private List<String> extractImprovements(JsonNode improvementsNode) {
		// TODO Auto-generated method stub
		List<String> improvements = new ArrayList<>();
		if(improvementsNode.isArray()) {
			for(JsonNode improvement : improvementsNode) {
				String area = improvement.path("area").asText();
				String recommendation = improvement.path("recommendation").asText();
				improvements.add(area + ": " + recommendation);
			}
		}
		return improvements.isEmpty() ? Collections.singletonList("No specific Improvements Provided") : improvements;
	}

	private void addAnalysis(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
		
		if(!analysisNode.path(key).isMissingNode()) {
			fullAnalysis.append(prefix)
			            .append(analysisNode.path(key).asText())
			            .append("\n\n");
		}
		// TODO Auto-generated method stub
		
	}

	private String createPromptForActivty(Activity activity) {
		return String.format("""
				Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
		        {
		          "analysis": {
		            "overall": "Overall analysis here",
		            "pace": "Pace analysis here",
		            "heartRate": "Heart rate analysis here",
		            "caloriesBurned": "Calories analysis here"
		          },
		          "improvements": [
		            {
		              "area": "Area name",
		              "recommendation": "Detailed recommendation"
		            }
		          ],
		          "suggestions": [
		            {
		              "workout": "Workout name",
		              "description": "Detailed workout description"
		            }
		          ],
		          "safety": [
		            "Safety point 1",
		            "Safety point 2"
		          ]
		        }

		        Analyze this activity:
		        Activity Type: %s
		        Duration: %d minutes
		        Calories Burned: %d
		        Additional Metrics: %s
		        
		        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
		        Ensure the response follows the EXACT JSON format shown above.
		        """,
		            		activity.getType(),
				            activity.getDuration(),
				            activity.getCaloriesBurned(),
				            activity.getAdditionalData()
      );
	}
}
