package com.sogoodlabs.plannerapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class PlannerApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlannerApiGatewayApplication.class, args);
	}

}
