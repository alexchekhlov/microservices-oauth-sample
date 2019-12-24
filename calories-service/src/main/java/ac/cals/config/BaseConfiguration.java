package ac.cals.config;

import ac.cals.domain.MealEntryEntity;
import ac.cals.dto.MealEntryDTO;
import ac.common.jpa.converter.PageRequestToPageableConverter;
import ac.common.jpa.converter.PageRequestToPageableConverterImpl;
import ac.common.jpa.criteria.CommonQueryParser;
import ac.common.jpa.criteria.QueryParser;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfiguration {
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(MealEntryEntity.class, MealEntryDTO.class).addMappings(mapper -> mapper.map(src -> src.getUser().getUsername(), MealEntryDTO::setUsername));
		return modelMapper;
	}

	@Bean
	public QueryParser queryParser() {
		return new CommonQueryParser();
	}

	@Bean
	public PageRequestToPageableConverter pageRequestToPageableConverter(){
		return new PageRequestToPageableConverterImpl();
	}
}
