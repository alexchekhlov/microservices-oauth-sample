package ac.system.auth.config;

import ac.common.jpa.converter.PageRequestToPageableConverter;
import ac.common.jpa.converter.PageRequestToPageableConverterImpl;
import ac.common.jpa.criteria.CommonQueryParser;
import ac.common.jpa.criteria.QueryParser;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BaseConfiguration {

	@Bean
	public QueryParser usersCriteriaParser() {
		return new CommonQueryParser();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}

	@Bean
	public PageRequestToPageableConverter pageRequestToPageableConverter(){
		return new PageRequestToPageableConverterImpl();
	}
}
