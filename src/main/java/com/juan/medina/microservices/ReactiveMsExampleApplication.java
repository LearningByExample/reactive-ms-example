package com.juan.medina.microservices;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactiveMsExampleApplication {

	final static Logger logger = Logger.getLogger(ReactiveMsExampleApplication.class);

	public static void main(String[] args) {
		logger.error("Starting application");
		SpringApplication.run(ReactiveMsExampleApplication.class, args);
	}
}
