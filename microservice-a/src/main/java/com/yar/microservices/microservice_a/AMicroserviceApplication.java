package com.yar.microservices.microservice_a;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(value = "com.yar.microservices.*")
@EnableOAuth2Sso
@Configuration
@EnableAutoConfiguration
public class AMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AMicroserviceApplication.class, args);
	}



}
