package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class AppConfiguracion {
	@Bean
	public ResourceBundleMessageSource messageSource() {
	ResourceBundleMessageSource messageSource
	= new ResourceBundleMessageSource();
	messageSource.setBasename("messages");
	messageSource.setDefaultEncoding("UTF-8");
	return messageSource;
	}

}
