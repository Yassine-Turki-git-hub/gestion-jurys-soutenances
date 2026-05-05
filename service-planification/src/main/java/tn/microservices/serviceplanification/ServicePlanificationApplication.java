package tn.microservices.serviceplanification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ServicePlanificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicePlanificationApplication.class, args);
	}

}
