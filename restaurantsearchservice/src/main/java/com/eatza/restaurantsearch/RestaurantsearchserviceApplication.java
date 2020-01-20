package com.eatza.restaurantsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableCaching
@EnableEurekaClient
public class RestaurantsearchserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantsearchserviceApplication.class, args);
	}
}
