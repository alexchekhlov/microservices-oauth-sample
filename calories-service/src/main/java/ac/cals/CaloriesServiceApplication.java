package ac.cals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableHystrix
public class CaloriesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaloriesServiceApplication.class, args);
	}
}
