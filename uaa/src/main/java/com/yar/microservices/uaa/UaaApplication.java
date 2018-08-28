package com.yar.microservices.uaa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@EnableEurekaClient
@SpringBootApplication
@ComponentScan("com.yar.microservices.uaa.*")
@Slf4j
@EnableAspectJAutoProxy
public class UaaApplication {

	private final WebMvcConfigurationSupport webMvcConfigurationSupport;

	private final ServletContext servletContext;


	@Autowired
	public UaaApplication(WebMvcConfigurationSupport webMvcConfigurationSupport, ServletContext servletContext) {
		this.webMvcConfigurationSupport = webMvcConfigurationSupport;
		this.servletContext = servletContext;

	}


	public static void main(String[] args) throws UnknownHostException {

		SpringApplication app = new SpringApplication(UaaApplication.class);
		Environment env = app.run(args).getEnvironment();
		String protocol = "http";
		log.info("\n----------------------------------------------------------\n\t" +
						"Application '{}' is running! Access URLs:\n\t" +
						"Local: \t\t{}://localhost:{}\n\t" +
						"External: \t{}://{}:{}\n\t" +
						"Profile(s): \t{}\n----------------------------------------------------------",
				env.getProperty("spring.application.name"),
				protocol,
				env.getProperty("server.port"),
				protocol,
				InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"),
				env.getActiveProfiles());

		String configServerStatus = env.getProperty("configserver.status");
		log.info("\n----------------------------------------------------------\n\t" +
						"Config Server: \t{}\n----------------------------------------------------------",
				configServerStatus == null ? "Not found or not setup for this application" : configServerStatus);

	}


	@PostConstruct
	public void initiate(){
		webMvcConfigurationSupport.setServletContext(servletContext);
	}

}
