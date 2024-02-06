package com.volasoftware.tinder;

import com.volasoftware.tinder.service.AuditorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
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
