package com.volasoftware.tinder;

import com.volasoftware.tinder.service.AuditorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class TinderApplication {

	@Bean
	public AuditorAware<String> auditorAware(){
		return new AuditorService();
	}
	public static void main(String[] args) {
		SpringApplication.run(TinderApplication.class, args);
	}

}
