package ac.cals.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class NutritionIXNutritionProvider implements NutritionProvider {

	@Value("${nutritionix.app.id}")
	private String applicationId;

	@Value("${nutritionix.api.key}")
	private String apiKey;

	@Value("${nutritionix.check.url}")
	private String checkUrl;

	@Override
	@HystrixCommand(fallbackMethod = "getDefaultCalories", commandKey = "nutritionix")
	public int getCalories(String meal) {
		WebClient webClient = WebClient.builder().baseUrl(checkUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader("x-app-id", applicationId)
				.defaultHeader("x-app-key", apiKey)
				.build();
		WebClient.RequestHeadersSpec request = webClient.post().body(BodyInserters.fromFormData("query", meal));

		JSONObject response = request
				.retrieve()
				.bodyToMono(JSONObject.class)
				.block();
		List<Map> foods = (List<Map>) response.get("foods");

		double sumOfCalories = foods.stream().map(food -> (((Number) food.get("nf_calories")).doubleValue())).reduce(0D, Double::sum);
		return (int) sumOfCalories;
	}

	private int getDefaultCalories(String s) {
		return 300;
	}
}
